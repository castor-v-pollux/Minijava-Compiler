package piglet;

import java.util.ArrayList;

/**
 * piglet�����װ�࣬�̳���{@link PCode}����ʾһ��������У��м���ܲ�������Label��
 *
 * @author castor_v_pollux
 */
public class PStmtList extends PCode {

	private ArrayList<PStmt> pStmts = new ArrayList<>();

	/**
	 * �������䡣{@code return this}���������������
	 */
	public PStmtList add(PStmt stmt) {
		pStmts.add(stmt);
		return this;
	}

	/**
	 * �����������䡣{@code return this}���������������
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
