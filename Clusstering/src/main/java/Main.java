import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {
    static Map<Integer, Vector> itemSet = new TreeMap<Integer, Vector>();
    static Map<Integer, Vector> initialCentroids = new TreeMap<Integer, Vector>();
    static Map<String , Vector> Centroids = new TreeMap<String, Vector>();
    static Map<String, Vector> BelongTo = new TreeMap<String, Vector>();
    static int NoOfRows;
    static int NoOfClusters;

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter number of rows you want to calc: ");
        NoOfRows = Integer.parseInt(input.nextLine());

        System.out.println("Enter number How many clustters you would like: ");
        NoOfClusters = Integer.parseInt(input.nextLine());

        // read file content
        readFile();

        //get initial cluster
        IntialC();

        IterareC();

        GetOutLiers();


    }

    public static void readFile() throws IOException {
        File myFile = new File("C://Users//MIRA//Desktop//Y4 T1//DM//Assignment2/CourseEvaluation.xlsx");
        FileInputStream fis = new FileInputStream(myFile);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);

        // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);

        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = mySheet.iterator();
        for (int i = 0; i < NoOfRows; i++) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Vector<Double> v = new Vector<Double>();
            String ProcessName="";
            String Atr="";
            int ProcessID=0;
            if (i > 0) {
                for (int j = 0; j < 21; j++) {
                    Cell cell = cellIterator.next();
                    if (j == 0) {
                        ProcessName = cell.toString();
                        ProcessName=ProcessName.substring(0,7);
                        ProcessID = Integer.parseInt(ProcessName.trim());
                    } else {
                        Atr=cell.toString();
                        Double temp=Double.parseDouble(Atr);
                        v.add(temp);
                        //System.out.println(i + " " + cell.toString() + " " + cell.getColumnIndex() + " " + j);
                    }
                }
                itemSet.put(ProcessID, v);
            }
        }
    }

    public static void IntialC() throws IOException {
        System.out.println("Iteration number: 1");
        int number = 0;
        for (int i = 0; i < NoOfClusters; i++) {
            number = 1+ (int) (Math.random() * ((NoOfRows - 1)));
            number = 2020000 + number;
            initialCentroids.put(number, itemSet.get(number));
            BelongTo.put(String.valueOf(number),new Vector<>());
        }
        while (initialCentroids.size() < NoOfClusters){
            number = 1+ (int) (Math.random() * ((NoOfRows - 1)));
            number = 2020000 + number;
            initialCentroids.put(number, itemSet.get(number));
            BelongTo.put(String.valueOf(number),new Vector<>());
        }

        System.out.println("Random Centroids "+ initialCentroids);
        Vector<Double> temp= new Vector<>();
        Vector<Double> centroidtemp= new Vector<>();
        Vector<Integer> tempVec= new Vector<>();
        Vector<String> CentroidsID= new Vector<>();
        for (Map.Entry<Integer, Vector> entry2 : initialCentroids.entrySet()) {
            String t= entry2.getKey().toString();
            CentroidsID.add(t);
        }
        for (Map.Entry<Integer, Vector> entry : itemSet.entrySet()) {
            System.out.println("Student ID"+ entry.getKey());
            Map<Double, String> min = new TreeMap<Double, String>();
            for (Map.Entry<Integer, Vector> entry2 : initialCentroids.entrySet()) {
                temp=entry.getValue();
                centroidtemp=entry2.getValue();
                Double distance=0.0;
                for(int i=0; i<temp.size(); i++) {
                    distance += (temp.get(i) - centroidtemp.get(i)) * (temp.get(i) - centroidtemp.get(i));
                }
                distance=Math.sqrt(distance);
                min.put(distance, String.valueOf(entry2.getKey()));
                if(entry.getKey().equals(entry2.getKey())){
                    String t= String.valueOf(entry2.getKey());
                    tempVec=BelongTo.get(t);
                    tempVec.add(entry.getKey());
                    BelongTo.put(String.valueOf(entry2.getKey()),tempVec);
                }
            }
//at8ert
            String t= String.valueOf(entry.getKey());
        if (!(CentroidsID.contains(t))) {
            String first = min.get(((TreeMap<Double, String>) min).firstKey());
            System.out.println("Distances from" + entry.getKey() + " to centroids: ");
            System.out.println(min);
            System.out.println("best match: ");
            System.out.println(first);
            System.out.println();
            tempVec = BelongTo.get(first);
            tempVec.add(entry.getKey());
            BelongTo.put(String.valueOf(first), tempVec);
            //BelongTo.put()
            min.clear();
        }
        }
        System.out.println();
        System.out.println();
        System.out.println("Centroids and points that belong to them:");
        System.out.println(BelongTo);
        System.out.println();
        System.out.println();
    }
    //a7eanan bttl3 fadya
    public static void IterareC() throws IOException {
        boolean finalIteration=true;
        int p=1;
        while (finalIteration) {
            int iteratonNo=p+1;
            System.out.println("Iteration number"+ iteratonNo);
            Map<String, Vector> oldBelongTo = new HashMap<>();
            for (Map.Entry<String, Vector> entry : BelongTo.entrySet()) {
                oldBelongTo.put(entry.getKey(),
                        entry.getValue());
            }
            BelongTo.clear();

            for (Map.Entry<String, Vector> entry : oldBelongTo.entrySet()) {
                String t = String.valueOf(entry.getValue());
                BelongTo.put(t, new Vector());
            }
            //System.out.println("BelongTo " + BelongTo);
            // calculate centroids mean
            Centroids.clear();
            for (Map.Entry<String, Vector> entry : oldBelongTo.entrySet()) {
                Vector<Integer> meanCen = new Vector<>();
                meanCen = entry.getValue();
                Vector<Vector<Double>> values = new Vector<>();
                for (int i = 0; i < meanCen.size(); i++) {
                    Vector<Double> valueOfEachCentroid = new Vector<>();
                    valueOfEachCentroid = itemSet.get(meanCen.get(i));
                    values.add(valueOfEachCentroid);

                }

                Vector<Double> newMeanCentrAtr = new Vector<>();
                Double sum = 0.0;
                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < values.size(); j++) {
                        sum += values.get(j).get(i);
                    }
                    sum = sum / values.size();
                    newMeanCentrAtr.add(sum);
                    sum = 0.0;
                }
                Centroids.put(meanCen.toString(), newMeanCentrAtr);
            }

            Vector<Double> temp = new Vector<>();
            Vector<Double> centroidtemp = new Vector<>();
            Vector<Integer> tempVec = new Vector<>();
            System.out.println("Centroids: ");
            System.out.println(Centroids);
            for (Map.Entry<Integer, Vector> entry : itemSet.entrySet()) {
                System.out.println("Student ID" + entry.getKey());
                Map<Double, String> min = new TreeMap<Double, String>();
                for (Map.Entry<String, Vector> entry2 : Centroids.entrySet()) {
                    temp = entry.getValue();
                    centroidtemp = entry2.getValue();
                    Double distance = 0.0;
                    for (int i = 0; i < temp.size(); i++) {
                        distance += (temp.get(i) - centroidtemp.get(i)) * (temp.get(i) - centroidtemp.get(i));
                    }
                    distance = Math.sqrt(distance);
                    min.put(distance, String.valueOf(entry2.getKey()));
                }
                Vector<Integer> v = new Vector<>();
                String first = min.get(((TreeMap<Double, String>) min).firstKey());
                System.out.println("Distances from" + entry.getKey() + " to centroids: ");
                System.out.println(min);
                System.out.println("best match: ");
                System.out.println(first);
                System.out.println();
                tempVec = BelongTo.get(first);
                tempVec.add(entry.getKey());
                BelongTo.put(String.valueOf(first), tempVec);
                min.clear();

            }


            System.out.println();
            System.out.println();
            System.out.println("Centroids and points that belong to them:");
            System.out.println(BelongTo);
            System.out.println();
            System.out.println();
            //System.out.println("Centroids and points that last time:");
            //System.out.println(oldBelongTo);

            //System.out.println(oldBelongTo==BelongTo);
            finalIteration=false;
            for (Map.Entry<String, Vector> entry : BelongTo.entrySet()) {
                String S= String.valueOf(entry.getValue());
                if(!(S.equals(entry.getKey()) && p!=1)){
                    finalIteration=true;
                }
            }
            //System.out.println(finalIteration);
            p++;
        }
    }

    public static void GetOutLiers() throws IOException {
        int min=10000;
        Vector<String> outliers = new Vector<>();
        for (Map.Entry<String, Vector> entry : BelongTo.entrySet()) {
            if(entry.getValue().size()<=min){
                min=entry.getValue().size();
                outliers=entry.getValue();
            }
        }
        System.out.println("outliers are: "+ outliers);
    }
    }

