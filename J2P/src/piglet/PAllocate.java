package piglet;

/**
 * piglet�����װ�࣬�̳���{@link PExp}����ʾHALLOCATE���ʽ��
 *
 * @author castor_v_pollux
 */
public class PAllocate extends PExp {

	private PExp pExp;

	public PAllocate(PExp pExp) {
		this.pExp = pExp;
	}

	@Override
	public int print(int depth) {
		System.out.print("HALLOCATE ");
		int l = pExp.print(depth + 10);
		if (l == MULTILINE)
			return MULTILINE;
		else
			return l + 10;
	}

}
