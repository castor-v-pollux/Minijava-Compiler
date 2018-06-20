package piglet;

/**
 * <p>该抽象类为piglet代码块的基类，规定了piglet代码包装类的基本接口规范。
 *
 * @author castor_v_pollux
 */
public abstract class PCode {

	/**
	 * 一级语句组的缩进宽度，建议大于4（否则三位数的label会占满4位缩进空间）
	 */
	protected static final int TAB_WIDTH = 6;

	/**
	 * 标记常量，用来表示该代码块会跨行
	 */
	protected static final int MULTILINE = -1;

	/**
	 * <p>对于单行代码段，直接打印该代码；
	 * <p>对于多行代码段，在当前缩进宽度depth位置下输出该代码段。
	 * @param depth 对于多行代码，当前代码块的缩进宽度
	 * @return 对于单行代码，返回输出的字符数，对于多行代码，返回{@link PCode#MULTILINE}。
	 */
	public abstract int print(int depth);

	/**
	 * 工具方法，从当前位置往右缩进depth个空格
	 * @param depth 缩进宽度
	 */
	protected void indent(int depth) {
		for (int i = 0; i < depth; i++)
			System.out.print(' ');
	}

	/**
	 * 工具方法，输出一个空格
	 */
	protected void space() {
		System.out.print(' ');
	}

}
