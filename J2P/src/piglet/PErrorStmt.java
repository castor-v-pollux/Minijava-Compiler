package piglet;

/**
 * <p>piglet代码包装类，继承自{@link PStmt}，表示ERROR语句。
 * <p>由于ERROR语句形式唯一，PErrorStmt使用单例类。
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
