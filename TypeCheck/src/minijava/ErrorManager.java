package minijava;

/**
 * 类型检查错误的处理类。所有的类型检查错误都会集中到这里，可以在该类中进行定制化的处理。
 *
 * @author castor_v_pollux
 */
public class ErrorManager {

	/**
	 * 错误处理。目前的处理方式为打印错误信息并退出程序
	 * @param error 类型检查的错误
	 */
	public static void error(TypeCheckError error) {
		System.out.println(String.format("Row %d, Col %d: %s", error.getRow(), error.getColumn(), error.getMessage()));
		System.exit(0);
	}

}
