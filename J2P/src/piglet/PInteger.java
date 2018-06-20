package piglet;

/**
 * piglet代码包装类，继承自{@link PExp}，表示一个piglet常数表达式。
 *
 * @author castor_v_pollux
 */
public class PInteger extends PExp {

	private int value;

	public PInteger(int value) {
		this.value = value;
	}

	@Override
	public int print(int depth) {
		String s = String.valueOf(value);
		System.out.print(s);
		return s.length();
	}

}
