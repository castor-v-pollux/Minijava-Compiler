package piglet;

/**
 * piglet代码包装类，继承自{@link PExp}，表示一个piglet Label名，用于{@link PJump}和{@link PCJump}中。
 *
 * @author castor_v_pollux
 */
public class PIdentifier extends PExp {

	private String identifier;

	public PIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public int print(int depth) {
		System.out.print(identifier);
		return identifier.length();
	}

}
