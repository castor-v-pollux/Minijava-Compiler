package s2k;

import java.io.InputStream;

import parser.ParseException;
import parser.SpigletParser;
import symbol.SymbolTable;
import syntaxtree.Goal;
import visitor.BuildFlowGraphVisitor;
import visitor.BuildSymbolTableVisitor;
import visitor.TranslateVisitor;

/**
 * <p>����Ϊ���Ĵ���ҵ�����࣬����SPiglet���뷭���Kanga���롣
 * <p>˼·Ϊ����Spiglet��Kanga��Ҫ�Ǵ���Ĵ����������⣬�������Ϊ�Ĳ���ʹ��������Visitor:
 * <ul>
 * <li>��һ��ʹ��{@link BuildSymbolTableVisitor}��ÿ�������е������б�ţ�����ÿ����������������ϵ��
 * <li>�ڶ���ʹ��{@link BuildFlowGraphVisitor}��ÿ����������佨��Ϊһ����ͼ�������Է���ʹ�á�
 * <li>������ʹ��{@link SymbolTable#LinearScan()}��ÿ�������ڲ����л��Է����ͼĴ������䡣
 * <li>���Ĳ�ʹ��{@link TranslateVisitor}���õ������õ��ļĴ����������������ķ��롣
 * </ul>
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
		Goal goal;
		try {
			goal = new SpigletParser(in).Goal();
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		goal.accept(new BuildSymbolTableVisitor(), null);
		goal.accept(new BuildFlowGraphVisitor(), null);
		SymbolTable.LinearScan();
		goal.accept(new TranslateVisitor(), null);
	}

}
