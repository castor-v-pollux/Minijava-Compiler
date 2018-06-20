package piglet;

import java.util.ArrayList;

/**
 * piglet代码包装类，继承自{@link PCode}，表示一个语句序列，中间可能插入若干Label。
 *
 * @author castor_v_pollux
 */
public class PStmtList extends PCode {

	private ArrayList<PStmt> pStmts = new ArrayList<>();

	/**
	 * 添加新语句。{@code return this}方便进行连续调用
	 */
	public PStmtList add(PStmt stmt) {
		pStmts.add(stmt);
		return this;
	}

	/**
	 * 批量添加新语句。{@code return this}方便进行连续调用
	 */
	public PStmtList addAll(ArrayList<PStmt> stmtList) {
		pStmts.addAll(stmtList);
		return this;
	}

	@Override
	public int print(int depth) {
		boolean hasLabel = false;
		for (PStmt stmt : pStmts) {
			if (stmt instanceof PLabel) {
				indent(depth - TAB_WIDTH);
				int l = stmt.print(depth);
				indent(TAB_WIDTH - l);
				hasLabel = true;
			} else {
				if (hasLabel)
					hasLabel = false;
				else
					indent(depth);
				stmt.print(depth);
			}
		}
		return MULTILINE;
	}

}
