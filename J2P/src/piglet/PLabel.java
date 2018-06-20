package piglet;

/**
 * <p>piglet代码包装类，继承自{@link PStmt}，表示一个Label语句标签。
 * <p>维护一个全局计数器用来获得新Label的id。
 *
 * @author castor_v_pollux
 */
public class PLabel extends PStmt {

	private static int LabelCnt = 0;

	private int id;

	private PLabel(int id) {
		this.id = id;
	}

	public static PLabel newLabel() {
		return new PLabel(LabelCnt++);
	}

	@Override
	public int print(int depth) {
		String s = String.format("L%d", id);
		System.out.print(s);
		return s.length();
	}

}
