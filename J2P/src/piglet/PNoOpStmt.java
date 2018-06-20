package piglet;

/**
 * <p>piglet�����װ�࣬�̳���{@link PStmt}����ʾNOOP��䡣
 * <p>����NOOP�����ʽΨһ��PNoOpStmtʹ�õ����ࡣ
 *
 * @author castor_v_pollux
 */
public class PNoOpStmt extends PStmt {

	private static PNoOpStmt instance = new PNoOpStmt();

	private PNoOpStmt() {
	}

	public static PNoOpStmt getInstance() {
		return instance;
	}

	@Override
	public int print(int depth) {
		System.out.println("NOOP");
		return MULTILINE;
	}

}
