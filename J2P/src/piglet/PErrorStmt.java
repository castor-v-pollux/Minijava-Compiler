package piglet;

/**
 * <p>piglet�����װ�࣬�̳���{@link PStmt}����ʾERROR��䡣
 * <p>����ERROR�����ʽΨһ��PErrorStmtʹ�õ����ࡣ
 *
 * @author castor_v_pollux
 */
public class PErrorStmt extends PStmt {

	private static PErrorStmt instance = new PErrorStmt();

	private PErrorStmt() {
	}

	public static PErrorStmt getInstance() {
		return instance;
	}

	@Override
	public int print(int depth) {
		System.out.println("ERROR");
		return MULTILINE;
	}

}
