package piglet;

import symbol.MType;

/**
 * piglet代码包装类，继承自{@link PCode}，表示一个piglet表达式。
 *
 * @author castor_v_pollux
 */
public abstract class PExp extends PCode {

	/**
	 * 表达式类型，在对该表达式进行方法调用时需要用到其类信息
	 */
	private MType type;

	public PExp setType(MType type) {
		this.type = type;
		return this;
	}

	public MType getType() {
		return type;
	}

	/**
	 * <p>对于piglet表达式语句块，表达式输出完成后不加空格、换行等空白符，空白符由使用表达式的语句进行维护。
	 * <p>若表达式本身为单行，返回表达式长度，若表达式本身为多行，返回{@link PCode#MULTILINE}。
	 */
	@Override
	public abstract int print(int depth);

}
