package Unixscheduling;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import Unixscheduling.UnixScheduling;
import Unixscheduling.UnixScheduling.ButtonListener;
public class UnixScheduling extends JFrame//Java_GUI�� �̿��� UnixScheduling ��� ǥ��
{
   String prior[]=null;//���Ͽ��� �ҷ� ���� �켱���� ����
   String names[] = null;//���Ͽ��� �ҷ� ���� ���μ��� �̸� ����
   public class ButtonListener implements ActionListener// JFrame�� Button �׼��� �߰� �����ϱ� ���� Ŭ���� ���� 
   {
      String out_string;//���Ͽ��� �ҷ��� ���� �ؽ�Ʈâ�� ���� ���� ���ڿ� ����
      int sizeOfFile=0;//������ ������ �� ������ ������ ����Ǿ� �ִ� ������ ũ��
      //String[] statement = null;
      JFileChooser chooser = new JFileChooser();//���� ����
      File newFile = null;
      public String StartLoad() throws FileNotFoundException//���Ͽ� ����Ǿ� �ִ� ���� �ҷ����� �Լ�(Start Button�� ���)
      {
         int temp_n=0;//�� ���μ��� �̸�
         int temp_p=0;//�� ���μ��� �켱����
         int inputDivide=0;// ����Ǿ� �ִ� ���ڿ��� ���μ����� �̸��� �켱������ ������ ���� ����
         File selectedFile = null;//���� ����
         if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)//���Ͽ� ����Ǿ� �ִ� ������ �ҷ� ���� ���� ���
         {
            selectedFile = chooser.getSelectedFile();
         }
         else 
         {
            System.exit(1);
         }
         Scanner reader =  new Scanner(selectedFile);//���õ� ���Ͽ� ����Ǿ� �ִ� ���� ���پ� �о�� �ؽ�Ʈ�� ���
         while(reader.hasNextLine())//���Ͽ� ����Ǿ� �ִ� ������ ���� ������ ������ �о� ���� ��� ����
         {
            reader.nextLine();
            sizeOfFile++;
         }
         String in_string = "";
         reader.close();
         prior = new String[sizeOfFile];//���μ��� �켱���� ����
         names = new String[sizeOfFile];//���μ��� �̸� ����
         reader =  new Scanner(selectedFile);//���õ� ���Ͽ� ����Ǿ� �ִ� ���� ���پ� �о� ���� ��� ����
         while(reader.hasNext())//���Ͽ��� ����� ������ ���������� loop����
         {
            if(inputDivide%3==0)//���μ��� �̸� ����
            {
               names[temp_n]=reader.next();
               in_string+=names[temp_n++]+" ";//�ؽ�Ʈ���Ͽ� ���
            }
            else if(inputDivide%3==2)//���μ��� �켱 ���� ����
            {
               prior[temp_p]=reader.next();
               in_string += prior[temp_p++]+"\n";//�ؽ�Ʈ���Ͽ� ���
            }
            else
            {
               in_string += reader.next()+" ";
            }
            inputDivide++;
         } 
         reader.close();
         return in_string;
      }
      public void AddNiceButton() throws FileNotFoundException//NiceValue�� ���� �߰� ��Ű�� ���� �Լ�
      {
         if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
         {
            newFile = chooser.getSelectedFile();
         }
         else
         {
            System.exit(1);
         }
         PrintWriter out = new PrintWriter(newFile);//���Ͽ� ������ ���� ����
         out.println(out_string);
         out.close();
      }
      @Override
      public void actionPerformed(ActionEvent e)//��ư�� �������� �۾��� ����ǰ� �ϱ����� ���
      {
         Object source = e.getSource();
         if(e.getSource()==niceButton)//AddNiceButton()�Լ� ����
         {
            try
            {
               out_string = memoField.getText();//�޸��ʵ忡 ���� �����Ͱ� �ؽ�Ʈ ���Ϸ� ����
               AddNiceButton();
            } 
            catch (FileNotFoundException e1)
            {
               e1.printStackTrace();
            }   
         }
         else if(e.getSource()==loadButton)//StartLoad()�Լ� ����
         {
            try 
            {
               memoField.setText(StartLoad());//�ؽ�Ʈ �ʵ忡 ���Ͽ� ����Ǿ� �ִ� �����͸� ǥ��
               Priority(names,prior);
            } 
            catch (FileNotFoundException e1) 
            {
               e1.printStackTrace();
            } 
            catch (InterruptedException e1)
            {
               e1.printStackTrace();
            }
         }
         
      }
      public void Priority(String[] means, String[] inputpriority) throws InterruptedException//�켱������ ���ϱ� ���� �Լ�(���μ��� �̸�,�� ���μ����ǿ켱 ������ �Ķ���� ������ �Է� �޴´�)
      {
         int baseCpucount=60;//Cpu counter�� 60������ �ö󰡰� ����
         int basePriority[]=new int[sizeOfFile];//���̽� �켱����
         int[] priority=new int[sizeOfFile];//cpuī��Ʈ + ���̽� �켱����
         int[] cpucount=new int[sizeOfFile];//�� ���μ����� cpuī��Ʈ
         int minPrior=0;// �켱������ ���� ���� ���� ã�Ƽ� �����ϱ� ���� �ε���
         final int KERNER_MODE = 0;//Ŀ�� ����� ���
         final int USER_MODE = 1;//����� ����� ���
         int minMode, currentMode;//���� ���μ����� ��� ������ ���� ���
         for(int c=0;c<sizeOfFile;c++)//�迭 �ʱ�ȭ
         {
            basePriority[c]=(Integer.parseInt(inputpriority[c]));//���Ͽ��� ������ �о� �帱�� ���ڿ��� �޾� ���̱⶧���� �켱������ ���� ����ؾ��ϱ� ������ ���������� ���� ����ȯ
            priority[c] = basePriority[c];//�⺻ �켱���� ����
         }
         for(int a=0;a<10;a++)//�� 10�� ���μ����� ����ȴٴ� �����Ͽ� ���� 
         {
            minPrior=0;//���� ���� �켱����
            if(means[minPrior].startsWith("Ŀ��"))//���� ���� �켱������ �̸��� Ŀ�η� ���۵ɶ� �� ���� Ŀ�� ����̸� �ƴ� ��� ����� ���� ǥ��
               minMode = KERNER_MODE;
            else 
            	minMode = USER_MODE;
            
            for(int i=1;i<sizeOfFile;i++)//���� ���� �켱������ �̸��� Ŀ�η� ���۵ɶ� �� ���� Ŀ�� ����̸� �ƴ� ��� ����� ���� ǥ��
            {
               if(means[i].startsWith("Ŀ��"))
                 currentMode = KERNER_MODE;
              else 
            	  currentMode = USER_MODE;
               if((currentMode<minMode) || (currentMode==minMode && priority[i]<priority[minPrior] ))//�����Ǹ���� �켱������ �������� �켱������ ���� ��庸�� ������ �Ǵ� ���� �켱 ������ ������ �׸��� ������ �켱������ ������ ��
               {
                  minPrior=i;//������ �켱������ ���������� �ʱ�ȭ(�迭�� ���� �Ǿ� �ִ� ���� �ε��� ���� ���� ��Ű�� ��)
               }
            }
            for(int i=0;i<baseCpucount;i++)//CpuCount ������ ���� ���
            {
               cpucount[minPrior]=cpucount[minPrior]+1;
               memoField.append("���μ��� " + means[minPrior] + "�� �켱���� " + priority[minPrior]+ " CPUī��Ʈ " + cpucount[minPrior] +"\n");
            }
            
            memoField.append("----------------------------------------------------------------"+"\n");
            for(int k=0;k<sizeOfFile;k++)//��� ���
            {
               cpucount[k]=cpucount[k]/2;
               priority[k]=basePriority[k]+cpucount[k]/2;
               memoField.append("���μ��� " + means[k] + "�� �켱���� " + priority[k] + " CPUī��Ʈ " + cpucount[k] +"\n");
            }
            
             memoField.append("----------------------------------------------------------------"+"\n");
         }
      }
   }
   private JTextArea memoField = new JTextArea(30,40);//�޸��ʵ��� ũ�� ����
   private JScrollPane memoPane = new JScrollPane(memoField);//��ũ���� ������ �޸� �ʵ� ����
   private JPanel mainPanel = new JPanel();
   private JPanel buttonPanel = new JPanel();
   private JButton loadButton = new JButton("START");//START��ư ����
   private JButton niceButton = new JButton("AddNiceValue");//AddNiceValue��ư ����
   private ActionListener listener = new ButtonListener();
   private void setButtonPanel() //��ư�� ���Ұ� ��ư �߰��� �׸��߰�
   {
      buttonPanel.setLayout(new FlowLayout());
      buttonPanel.add(loadButton);
      buttonPanel.add(niceButton);
      loadButton.addActionListener(listener);
      niceButton.addActionListener(listener);
      add(buttonPanel,BorderLayout.SOUTH);
   }
   public UnixScheduling() 
   {
      setSize(1000, 1000);
      setTitle("�ü�� ���н� �����⸵ ���");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new BorderLayout());
      setButtonPanel();
      mainPanel.add(memoPane);
      add(mainPanel,BorderLayout.CENTER);
      pack();
   }
   public static void main(String[] args) 
   {
      JFrame frame = new UnixScheduling();
      frame.setVisible(true);
   }

}





