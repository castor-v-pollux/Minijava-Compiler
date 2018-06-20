package piglet;

import java.util.ArrayList;

/**
 * piglet�����װ�࣬�̳���{@link PCode}����ʾһ��������pigletԴ����
 *
 * @author castor_v_pollux
 */
public class PGoal extends PCode {

	private PStmtList pStmtList;

	private ArrayList<PProcedure> pProcedures = new ArrayList<>();

	public PGoal(PStmtList pStmtList) {
		this.pStmtList = pStmtList;
	}

	public void addAll(ArrayList<PProcedure> procedureList) {
		pProcedures.addAll(procedureList);
	}

	@Override
	public int print(int depth) {
		// Ĭ��depth=0����ȥMAIN��END�ȵ���������
		System.out.println("MAIN");
		pStmtList.print(TAB_WIDTH);
		System.out.println("END");
		for (PProcedure procedure : pProcedures)
			procedure.print(0);
		return MULTILINE;
	}

}
