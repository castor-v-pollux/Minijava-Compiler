package piglet;

/**
 * piglet代码包装类，继承自{@link PCode}，表示一个piglet语句。
 *
 * @author castor_v_pollux
 */
public abstract class PStmt extends PCode {

	/**
	 * <p>对于piglet语句块，无论语句本身是否为多行，输出完成后都进行换行，上层只进行语句前的缩进操作。
	 * <p>必返回{@link PCode#MULTILINE}。
	 */
	@Override
	public abstract int print(int depth);

}
