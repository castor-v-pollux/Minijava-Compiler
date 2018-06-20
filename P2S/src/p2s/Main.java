package p2s;

import java.io.InputStream;

import parser.ParseException;
import parser.PigletParser;
import spiglet.STemp;
import syntaxtree.Node;
import visitor.MaxTempVisitor;
import visitor.TranslateVisitor;

/**
 * <p>����Ϊ��������ҵ�����࣬����Piglet���뷭���SPiglet���롣
 * <p>˼·Ϊ������SPiglet������Piglet�е�Ƕ�ױ��ʽ���������ƣ�ֻ��Ҫ��Pigletÿ������У�
 * ���Ǹ�����õ��ı��ʽ�ǲ��Ƿ���SPiglet�淶�������������Ҫ�½��Ĵ���ʹ��MOVE���Ԥ����
 * <p>���ڷ���ʱ�����������ʹ�õı��ʽ����������Ҫ��SPiglet���ʽ���ͽ��д��룬���������ʽ����Ҫ�����װ���ַ������أ��ϲ�ֱ�Ӵ�ӡ��
 * �������²�Ԥ�Ƚ���Ԥ��������Ԥ����õ���TEMP��
 * 
 * <p>����SPiglet���������������SPiglet��������Piglet�����ĸ���Ƕ�ױ��ʽ����˲���Ҫ���ӵĴ����װ��ֱ�����������̨���ɡ�
 * ���ʱֻ��Ҫע�����ÿ����䣬��Ҫ��ǰ������һ��TAB��
 *
 * @author castor_v_pollux
 */
public class Main {

	public static void main(String[] args) {
		InputStream in = System.in;
		// try {
		// in = new FileInputStream(args[0]);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// return;
		// }
		Node goal;
		try {
			goal = new PigletParser(in).Goal();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return;
		}
		STemp.init(goal.accept(new MaxTempVisitor()));
		goal.accept(new TranslateVisitor(), null);
	}

}
