package piglet;

import java.util.ArrayList;

/**
 * piglet代码包装类，继承自{@link PExp}，表示CALL表达式。
 *
 * @author castor_v_pollux
 */
public class PCall extends PExp {

	private PExp pExpFunc;

	private ArrayList<PExp> pExpParams = new ArrayList<>();

	public PCall(PExp pExpFunc) {
		this.pExpFunc = pExpFunc;
	}

	public void addParam(PExp param) {
		pExpParams.add(param);
	}

	@Override
	public int print(int depth) {
		System.out.print("CALL ");
		depth += 5;
		pExpFunc.print(depth);// 函数地址默认为多行表达式
		int lParam, ldepth;
		System.out.println();
		indent(depth);
		System.out.print('(');
		ldepth = depth + 1;
		// 输出各个参数。若某个参数跨行，则下一个参数换行并与第一个参数对齐。
		int l = pExpParams.size();
		for (int i = 0; i < l; i++) {
			PExp exp = pExpParams.get(i);
			lParam = exp.print(ldepth);
			if (i == l - 1)
				break;
			if (lParam == MULTILINE) {
				System.out.println();
				ldepth = depth + 1;
				indent(ldepth);
			} else {
				ldepth += lParam + 1;
				space();
			}
		}
		System.out.print(')');
		return MULTILINE;
	}

}
