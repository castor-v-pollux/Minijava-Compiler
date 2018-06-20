package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PExp}����ʾһ��piglet Label��������{@link PJump}��{@link PCJump}�С�
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
