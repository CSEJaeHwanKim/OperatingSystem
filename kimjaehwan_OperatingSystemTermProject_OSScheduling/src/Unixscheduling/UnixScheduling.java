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
public class UnixScheduling extends JFrame//Java_GUI를 이용한 UnixScheduling 기법 표현
{
   String prior[]=null;//파일에서 불러 오는 우선순위 저장
   String names[] = null;//파일에서 불러 오는 프로세스 이름 저장
   public class ButtonListener implements ActionListener// JFrame에 Button 액션을 추가 수행하기 위한 클래스 설정 
   {
      String out_string;//파일에서 불러온 정보 텍스트창에 띄우기 위한 문자열 변수
      int sizeOfFile=0;//파일의 사이즈 즉 각각의 행으로 저장되어 있는 파일의 크기
      //String[] statement = null;
      JFileChooser chooser = new JFileChooser();//파일 선택
      File newFile = null;
      public String StartLoad() throws FileNotFoundException//파일에 저장되어 있는 값을 불러오는 함수(Start Button의 기능)
      {
         int temp_n=0;//각 프로세스 이름
         int temp_p=0;//각 프로세스 우선순위
         int inputDivide=0;// 저장되어 있는 문자열을 프로세스의 이름과 우선순위로 나누기 위한 변수
         File selectedFile = null;//파일 선택
         if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)//파일에 저장되어 있는 정보를 불러 오기 위한 기능
         {
            selectedFile = chooser.getSelectedFile();
         }
         else 
         {
            System.exit(1);
         }
         Scanner reader =  new Scanner(selectedFile);//선택된 파일에 저장되어 있는 값을 한줄씩 읽어와 텍스트에 출력
         while(reader.hasNextLine())//파일에 저장되어 있는 정보가 없을 때까지 정보를 읽어 오는 기능 수행
         {
            reader.nextLine();
            sizeOfFile++;
         }
         String in_string = "";
         reader.close();
         prior = new String[sizeOfFile];//프로세스 우선순위 저장
         names = new String[sizeOfFile];//프로세스 이름 저장
         reader =  new Scanner(selectedFile);//선택된 파일에 저장되어 있는 값을 한줄씩 읽어 오는 기능 수행
         while(reader.hasNext())//파일에서 저장된 정보가 없을때까지 loop수행
         {
            if(inputDivide%3==0)//프로세스 이름 저장
            {
               names[temp_n]=reader.next();
               in_string+=names[temp_n++]+" ";//텍스트파일에 출력
            }
            else if(inputDivide%3==2)//프로세스 우선 순위 저장
            {
               prior[temp_p]=reader.next();
               in_string += prior[temp_p++]+"\n";//텍스트파일에 출력
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
      public void AddNiceButton() throws FileNotFoundException//NiceValue의 값을 추가 시키기 위한 함수
      {
         if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
         {
            newFile = chooser.getSelectedFile();
         }
         else
         {
            System.exit(1);
         }
         PrintWriter out = new PrintWriter(newFile);//파일에 정보가 씌여 저장
         out.println(out_string);
         out.close();
      }
      @Override
      public void actionPerformed(ActionEvent e)//버튼을 눌렀을때 작업이 수행되게 하기위해 사용
      {
         Object source = e.getSource();
         if(e.getSource()==niceButton)//AddNiceButton()함수 수행
         {
            try
            {
               out_string = memoField.getText();//메모필드에 적은 데이터가 텍스트 파일로 저장
               AddNiceButton();
            } 
            catch (FileNotFoundException e1)
            {
               e1.printStackTrace();
            }   
         }
         else if(e.getSource()==loadButton)//StartLoad()함수 수행
         {
            try 
            {
               memoField.setText(StartLoad());//텍스트 필드에 파일에 저장되어 있는 데이터를 표시
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
      public void Priority(String[] means, String[] inputpriority) throws InterruptedException//우선순위를 정하기 위한 함수(프로세스 이름,각 프로세스의우선 순위를 파라미터 변수로 입력 받는다)
      {
         int baseCpucount=60;//Cpu counter를 60까지만 올라가게 고정
         int basePriority[]=new int[sizeOfFile];//베이스 우선순위
         int[] priority=new int[sizeOfFile];//cpu카운트 + 베이스 우선순위
         int[] cpucount=new int[sizeOfFile];//각 프로세스의 cpu카운트
         int minPrior=0;// 우선순위가 가장 높은 것을 찾아서 수행하기 위한 인덱스
         final int KERNER_MODE = 0;//커널 모드일 경우
         final int USER_MODE = 1;//사용자 모드일 경우
         int minMode, currentMode;//현재 프로세스의 모드 구별을 위해 사용
         for(int c=0;c<sizeOfFile;c++)//배열 초기화
         {
            basePriority[c]=(Integer.parseInt(inputpriority[c]));//파일에서 정보를 읽어 드릴때 문자열로 받아 들이기때문에 우선순위의 경우는 계산해야하기 때문에 정수형으로 강제 형변환
            priority[c] = basePriority[c];//기본 우선순위 고정
         }
         for(int a=0;a<10;a++)//총 10번 프로세스가 실행된다는 가정하에 실행 
         {
            minPrior=0;//가장 작은 우선순위
            if(means[minPrior].startsWith("커널"))//가장 작은 우선순위의 이름이 커널로 시작될때 그 모드는 커널 모드이며 아닐 경우 사용자 모드로 표시
               minMode = KERNER_MODE;
            else 
            	minMode = USER_MODE;
            
            for(int i=1;i<sizeOfFile;i++)//가장 작은 우선순위의 이름이 커널로 시작될때 그 모드는 커널 모드이며 아닐 경우 사용자 모드로 표시
            {
               if(means[i].startsWith("커널"))
                 currentMode = KERNER_MODE;
              else 
            	  currentMode = USER_MODE;
               if((currentMode<minMode) || (currentMode==minMode && priority[i]<priority[minPrior] ))//현재의모드의 우선순위가 가장작은 우선순위를 가진 모드보다 작을때 또는 서로 우선 순위가 같을때 그리고 기존의 우선순위가 더작을 때
               {
                  minPrior=i;//더작은 우선순위를 가진것으로 초기화(배열에 저장 되어 있는 것을 인덱스 값을 저장 시키는 것)
               }
            }
            for(int i=0;i<baseCpucount;i++)//CpuCount 증가에 따른 출력
            {
               cpucount[minPrior]=cpucount[minPrior]+1;
               memoField.append("프로세스 " + means[minPrior] + "의 우선순위 " + priority[minPrior]+ " CPU카운트 " + cpucount[minPrior] +"\n");
            }
            
            memoField.append("----------------------------------------------------------------"+"\n");
            for(int k=0;k<sizeOfFile;k++)//결과 출력
            {
               cpucount[k]=cpucount[k]/2;
               priority[k]=basePriority[k]+cpucount[k]/2;
               memoField.append("프로세스 " + means[k] + "의 우선순위 " + priority[k] + " CPU카운트 " + cpucount[k] +"\n");
            }
            
             memoField.append("----------------------------------------------------------------"+"\n");
         }
      }
   }
   private JTextArea memoField = new JTextArea(30,40);//메모필드의 크기 조장
   private JScrollPane memoPane = new JScrollPane(memoField);//스크롤이 가능한 메모 필드 생성
   private JPanel mainPanel = new JPanel();
   private JPanel buttonPanel = new JPanel();
   private JButton loadButton = new JButton("START");//START버튼 생성
   private JButton niceButton = new JButton("AddNiceValue");//AddNiceValue버튼 생성
   private ActionListener listener = new ButtonListener();
   private void setButtonPanel() //버튼의 역할과 버튼 추가및 그림추가
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
      setTitle("운영체제 유닉스 스케출링 기법");
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





