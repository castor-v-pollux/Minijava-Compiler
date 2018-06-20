package piglet;

import java.util.ArrayList;

/**
 * piglet�����װ�࣬�̳���{@link PExp}����ʾCALL���ʽ��
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
		pExpFunc.print(depth);// ������ַĬ��Ϊ���б��ʽ
		int lParam, ldepth;
		System.out.println();
		indent(depth);
		System.out.print('(');
		ldepth = depth + 1;
		// ���������������ĳ���������У�����һ���������в����һ���������롣
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
