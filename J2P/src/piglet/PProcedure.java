package piglet;

/**
 * piglet代码包装类，继承自{@link PCode}，表示一个piglet函数。
 *
 * @author castor_v_pollux
 */
public class PProcedure extends PCode {

	/**
	 * 函数名
	 */
	private String name;

	/**
	 * 参数个数，必须不超过20
	 */
	private int paramCnt;

	private PStmtExp pStmtExp;

	public PProcedure(String name, int paramCnt, PStmtExp pStmtExp) {
		this.name = name;
		this.paramCnt = paramCnt;
		this.pStmtExp = pStmtExp;
	}

	@Override
	public int print(int depth) {
		System.out.println(String.format("\n%s[%d]", name, paramCnt));
		pStmtExp.print(depth);
		System.out.println();
		return MULTILINE;
	}

}
