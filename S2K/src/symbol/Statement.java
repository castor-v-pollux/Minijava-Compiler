package symbol;

import java.util.HashSet;

/**
 * 表示一个Spiglet语句，记录其前驱后继以及对变量的使用、赋值情况，作为程序流图中的节点参与活性分析。
 *
 * @author castor_v_pollux
 */
public class Statement {

	/**
	 * 该语句的前驱和后继
	 */
	private HashSet<Statement> prev, next;

	/**
	 * 该语句所定义和使用的变量
	 */
	private HashSet<Integer> def, use;
	/**
	 * 该语句执行前后的活跃变量
	 */
	public HashSet<Integer> in, out;

	public Statement() {
		prev = new HashSet<>();
		next = new HashSet<>();
		def = new HashSet<>();
		use = new HashSet<>();
		in = new HashSet<>();
		out = new HashSet<>();
	}

	public void addNext(Statement statement) {
		next.add(statement);
		statement.prev.add(this);
	}

	public void addUse(int temp) {
		use.add(temp);
	}

	public void addDef(int temp) {
		def.add(temp);
	}

	/**
	 * 更新该节点的活跃变量集合
	 * @return 是否有变化
	 */
	public boolean activityAnalyze() {
		for (Statement next : next)
			out.addAll(next.in);
		HashSet<Integer> newIn = new HashSet<>();
		newIn.addAll(out);
		newIn.removeAll(def);
		newIn.addAll(use);
		if (!in.equals(newIn)) {
			in = newIn;
			return true;
		}
		return false;
	}

}
