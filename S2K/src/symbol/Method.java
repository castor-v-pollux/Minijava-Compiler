package symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Method {

	// For declaration
	/**
	 * 方法名
	 */
	private String name;
	/**
	 * 方法参数个数
	 */
	private int param;
	/**
	 * 方法使用的栈单元个数
	 */
	private int stack;
	/**
	 * 方法调用的最大参数个数
	 */
	private int callParam;
	/**
	 * 方法使用的callee-saved寄存器个数
	 */
	private int maxS;

	// For activity analysis
	/**
	 * 方法的语句列表
	 */
	private ArrayList<Statement> statements;
	/**
	 * 方法中调用语句的位置
	 */
	private HashSet<Integer> callPos;

	// For register allocation
	/**
	 * 每个变量的活跃区间
	 */
	private HashMap<Integer, Interval> tempInterval;
	/**
	 * 被分配为caller-saved寄存器的变量
	 */
	private HashMap<Integer, String> regT;
	/**
	 * 被分配为callee-saved寄存器的变量
	 */
	private HashMap<Integer, String> regS;
	/**
	 * 被溢出的变量
	 */
	private HashMap<Integer, String> regSpilled;

	public Method(String name, int param) {
		this.name = name;
		this.param = param;
		callParam = 0;
		maxS = 0;
		statements = new ArrayList<>();
		callPos = new HashSet<>();
		tempInterval = new HashMap<>();
		regT = new HashMap<>();
		regS = new HashMap<>();
		regSpilled = new HashMap<>();
	}

	/**
	 * 获取方法名
	 * @return 方法名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 获取方法头部的字符串
	 * @return 声明字符串
	 */
	public String getHeader() {
		return String.format("%s[%d][%d][%d]", name, param, stack, callParam);
	}

	public void add(Statement statement) {
		statements.add(statement);
	}

	public Statement get(int id) {
		return statements.get(id);
	}

	/**
	 * 在程序流图中添加一条从语句fromId到toId的边
	 * @param fromId 入
	 * @param toId 出
	 */
	public void addFlow(int fromId, int toId) {
		Statement from = statements.get(fromId);
		Statement to = statements.get(toId);
		from.addNext(to);
	}

	/**
	 * 添加一个调用语句的位置
	 * @param pos 语句序号
	 */
	public void addCall(int pos) {
		callPos.add(pos);
	}

	/**
	 * 更新最大调用参数数量
	 * @param param 调用参数
	 */
	public void updateCallParam(int param) {
		if (param > callParam)
			callParam = param;
	}

	/**
	 * 对一个变量更新其活跃区间
	 * @param temp 变量
	 * @param inteval 活跃区间
	 */
	public void addInteval(int temp, Interval inteval) {
		if (!tempInterval.containsKey(temp))
			tempInterval.put(temp, inteval);
	}

	/**
	 * 进行活性分析，不断迭代直到活性区间收敛
	 */
	public void activityAnalyze() {
		boolean done = false;
		while (!done) {
			done = true;
			for (int id = statements.size() - 1; id >= 0; id--) {
				Statement statement = statements.get(id);
				if (statement.activityAnalyze())
					done = false;
			}
		}
	}

	/**
	 * 计算所有变量的活性区间
	 */
	public void computeAllInteval() {
		for (int id = 0; id < statements.size(); id++) {
			Statement statement = statements.get(id);
			for (Integer temp : statement.in)
				tempInterval.get(temp).end = id;
		}
		for (Interval interval : tempInterval.values())
			for (int callPos : callPos)
				if (interval.begin < callPos && interval.end > callPos)
					interval.acrossCall = true;
	}

	/**
	 * <p>进行寄存器的实际分配，分配思路如下：
	 * <p>线性扫描所有变量的活跃区间，对于跨越函数调用的活跃区间，将其分配为callee-saved寄存器或进行溢出；
	 * <p>否则先检查caller-saved寄存器，若有空闲则进行分配；
	 * <p>否则与结束最晚的进行交换，并检查callee-saved寄存器，若有空闲则进行分配，
	 * <p>否则与结束最晚的进行交换，并溢出该变量。
	 */
	public void allocateRegisters() {
		ArrayList<Interval> intervals = new ArrayList<>();
		intervals.addAll(tempInterval.values());
		Collections.sort(intervals);

		// Current interval of each register
		Interval[] TInterval = new Interval[10];
		Interval[] SInterval = new Interval[8];

		for (Interval interval : intervals) {
			int lastT = -1, lastS = -1, emptyT = -1, emptyS = -1;
			// Check t0~t9 for available regT and last regT
			for (int t = 9; t >= 0; t--)
				if (TInterval[t] != null) {
					if (TInterval[t].end <= interval.begin) {
						// This interval has ended, record it and clear the register.
						regT.put(TInterval[t].tempId, "t" + t);
						TInterval[t] = null;
						emptyT = t;
					} else if (lastT == -1 || TInterval[t].end > TInterval[lastT].end)
						lastT = t;
				} else
					emptyT = t;
			// Check s0~s7 for available regS and last regS
			for (int s = 7; s >= 0; s--)
				if (SInterval[s] != null) {
					if (SInterval[s].end <= interval.begin) {
						// This interval has ended, record it and clear the register.
						regS.put(SInterval[s].tempId, "s" + s);
						maxS = Math.max(maxS, s);
						SInterval[s] = null;
						emptyS = s;
					} else if (lastS == -1 || SInterval[s].end > SInterval[lastS].end)
						lastS = s;
				} else
					emptyS = s;
			// Assign reg T
			if (!interval.acrossCall)
				if (emptyT != -1) {
					// put it in empty T
					TInterval[emptyT] = interval;
					interval = null;
				} else if (interval.end < TInterval[lastT].end) {
					// had better use the register for shorter interval
					Interval tmp = TInterval[lastT];
					TInterval[lastT] = interval;
					interval = tmp;
				}
			// Assign reg S
			if (interval != null)
				if (emptyS != -1) {
					// put it in empty S
					SInterval[emptyS] = interval;
					interval = null;
				} else if (interval.end < SInterval[lastS].end) {
					// had better use the register for shorter interval
					Interval tmp = SInterval[lastS];
					SInterval[lastS] = interval;
					interval = tmp;
				}
			// No T and S available any more, spill it
			if (interval != null)
				regSpilled.put(interval.tempId, null);
		}
		for (int t = 0; t < 10; t++)
			if (TInterval[t] != null)
				regT.put(TInterval[t].tempId, "t" + t);
		for (int s = 0; s < 8; s++)
			if (SInterval[s] != null) {
				regS.put(SInterval[s].tempId, "s" + s);
				maxS = Math.max(maxS, s);
			}
		// Stack units has 3 parts:
		// 1.params more than 4
		// 2.callee-saved S
		// 3.spilled regs
		int stackId = Math.max(param - 4, 0) + (maxS + 1);
		for (Integer tempId : regSpilled.keySet())
			regSpilled.put(tempId, "SPILLEDARG " + stackId++);
		stack = stackId;
	}

	/**
	 * 判断一个变量是否被溢出
	 * @param tempId 变量id
	 * @return 是否
	 */
	public boolean isSpilled(int tempId) {
		return regSpilled.containsKey(tempId);
	}

	/**
	 * 从一个变量中加载值
	 * @param tempId 变量id
	 * @param regTemp 可能需要的临时寄存器
	 * @return 值所在的寄存器
	 */
	public String getReg(int tempId, String regTemp) {
		if (regT.containsKey(tempId))
			return regT.get(tempId);
		else if (regS.containsKey(tempId))
			return regS.get(tempId);
		else {
			System.out.println(String.format("\tALOAD %s %s", regTemp, regSpilled.get(tempId)));
			return regTemp;
		}
	}

	/**
	 * 往一个变量中保存值
	 * @param tempId 变量id
	 * @param exp 需要保存的表达式
	 */
	public void putReg(int tempId, String exp) {
		if (regSpilled.containsKey(tempId)) {
			if (!exp.equals("v0"))
				System.out.println(String.format("\tMOVE v0 %s", exp));
			System.out.println(String.format("\tASTORE %s v0", regSpilled.get(tempId)));
		} else {
			String r = getReg(tempId, null);
			if (!r.equals(exp))
				System.out.println(String.format("\tMOVE %s %s", r, exp));
		}
	}

	/**
	 * 在方法开头保存所有的callee-saved寄存器
	 */
	public void storeReg() {
		int stackId = Math.max(param - 4, 0);
		for (int i = 0; i <= maxS; i++)
			System.out.println(String.format("\tASTORE SPILLEDARG %d s%d", i + stackId, i));
	}

	/**
	 * 在方法结尾恢复所有的callee-saved寄存器
	 */
	public void restoreReg() {
		int stackId = Math.max(param - 4, 0);
		for (int i = 0; i <= maxS; i++)
			System.out.println(String.format("\tALOAD s%d SPILLEDARG %d", i, i + stackId));
	}

	/**
	 * 在方法开头加载所有参数
	 */
	public void loadParam() {
		int i;
		for (i = 0; i < param && i < 4; i++)
			if (tempInterval.containsKey(i))
				putReg(i, "a" + i);
		for (; i < param; i++)
			if (tempInterval.containsKey(i))
				if (regSpilled.containsKey(i)) {
					System.out.println(String.format("\tALOAD v0 SPILLEDARG %d", i - 4));
					putReg(i, "v0");
				} else
					System.out.println(String.format("\tALOAD %s SPILLEDARG %d", getReg(i, null), i - 4));
	}

}
