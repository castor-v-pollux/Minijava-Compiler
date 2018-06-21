package symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Method {

	// For declaration
	/**
	 * ������
	 */
	private String name;
	/**
	 * ������������
	 */
	private int param;
	/**
	 * ����ʹ�õ�ջ��Ԫ����
	 */
	private int stack;
	/**
	 * �������õ�����������
	 */
	private int callParam;
	/**
	 * ����ʹ�õ�callee-saved�Ĵ�������
	 */
	private int maxS;

	// For activity analysis
	/**
	 * ����������б�
	 */
	private ArrayList<Statement> statements;
	/**
	 * �����е�������λ��
	 */
	private HashSet<Integer> callPos;

	// For register allocation
	/**
	 * ÿ�������Ļ�Ծ����
	 */
	private HashMap<Integer, Interval> tempInterval;
	/**
	 * ������Ϊcaller-saved�Ĵ����ı���
	 */
	private HashMap<Integer, String> regT;
	/**
	 * ������Ϊcallee-saved�Ĵ����ı���
	 */
	private HashMap<Integer, String> regS;
	/**
	 * ������ı���
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
	 * ��ȡ������
	 * @return ������
	 */
	public String getName() {
		return name;
	}

	/**
	 * ��ȡ����ͷ�����ַ���
	 * @return �����ַ���
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
	 * �ڳ�����ͼ�����һ�������fromId��toId�ı�
	 * @param fromId ��
	 * @param toId ��
	 */
	public void addFlow(int fromId, int toId) {
		Statement from = statements.get(fromId);
		Statement to = statements.get(toId);
		from.addNext(to);
	}

	/**
	 * ���һ����������λ��
	 * @param pos ������
	 */
	public void addCall(int pos) {
		callPos.add(pos);
	}

	/**
	 * ���������ò�������
	 * @param param ���ò���
	 */
	public void updateCallParam(int param) {
		if (param > callParam)
			callParam = param;
	}

	/**
	 * ��һ�������������Ծ����
	 * @param temp ����
	 * @param inteval ��Ծ����
	 */
	public void addInteval(int temp, Interval inteval) {
		if (!tempInterval.containsKey(temp))
			tempInterval.put(temp, inteval);
	}

	/**
	 * ���л��Է��������ϵ���ֱ��������������
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
	 * �������б����Ļ�������
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
	 * <p>���мĴ�����ʵ�ʷ��䣬����˼·���£�
	 * <p>����ɨ�����б����Ļ�Ծ���䣬���ڿ�Խ�������õĻ�Ծ���䣬�������Ϊcallee-saved�Ĵ�������������
	 * <p>�����ȼ��caller-saved�Ĵ��������п�������з��䣻
	 * <p>�������������Ľ��н����������callee-saved�Ĵ��������п�������з��䣬
	 * <p>�������������Ľ��н�����������ñ�����
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
	 * �ж�һ�������Ƿ����
	 * @param tempId ����id
	 * @return �Ƿ�
	 */
	public boolean isSpilled(int tempId) {
		return regSpilled.containsKey(tempId);
	}

	/**
	 * ��һ�������м���ֵ
	 * @param tempId ����id
	 * @param regTemp ������Ҫ����ʱ�Ĵ���
	 * @return ֵ���ڵļĴ���
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
	 * ��һ�������б���ֵ
	 * @param tempId ����id
	 * @param exp ��Ҫ����ı��ʽ
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
	 * �ڷ�����ͷ�������е�callee-saved�Ĵ���
	 */
	public void storeReg() {
		int stackId = Math.max(param - 4, 0);
		for (int i = 0; i <= maxS; i++)
			System.out.println(String.format("\tASTORE SPILLEDARG %d s%d", i + stackId, i));
	}

	/**
	 * �ڷ�����β�ָ����е�callee-saved�Ĵ���
	 */
	public void restoreReg() {
		int stackId = Math.max(param - 4, 0);
		for (int i = 0; i <= maxS; i++)
			System.out.println(String.format("\tALOAD s%d SPILLEDARG %d", i, i + stackId));
	}

	/**
	 * �ڷ�����ͷ�������в���
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
