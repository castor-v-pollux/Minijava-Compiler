package spiglet;

/**
 * SPiglet代码包装类，用来包装一个字符串
 *
 * @author castor_v_pollux
 */
public class SCode {

	private String code;

	public SCode() {

	}

	public SCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}

}
