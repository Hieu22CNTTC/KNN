import java.io.*;
import java.util.*;

public class KNN {
    // Lớp để lưu trữ một điểm dữ liệu
    static class DataPoint {
        double x1, x2;
        String label;

        public DataPoint(double x1, double x2, String label) {
            this.x1 = x1;
            this.x2 = x2;
            this.label = label;
        }
    }

    // Hàm tính khoảng cách Euclidean giữa hai điểm
    public static double euclideanDistance(DataPoint p1, DataPoint p2) {
        return Math.sqrt(Math.pow(p1.x1 - p2.x1, 2) + Math.pow(p1.x2 - p2.x2, 2));
    }

    // Hàm để tìm nhãn xuất hiện nhiều nhất từ danh sách k hàng xóm gần nhất
    public static String findMostFrequentLabel(List<String> labels) {
        Map<String, Integer> labelCount = new HashMap<>();
        for (String label : labels) {
            labelCount.put(label, labelCount.getOrDefault(label, 0) + 1);
        }

        String mostFrequentLabel = null;
        int maxCount = -1;
        for (Map.Entry<String, Integer> entry : labelCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequentLabel = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostFrequentLabel;
    }

    // Hàm thực hiện KNN với k hàng xóm gần nhất
    public static String classify(DataPoint testPoint, List<DataPoint> trainData, int k) {
        // Tạo một danh sách chứa các khoảng cách từ điểm kiểm thử tới mỗi điểm trong tập huấn luyện
        List<DataPoint> nearestNeighbors = new ArrayList<>(trainData);
        nearestNeighbors.sort(Comparator.comparingDouble(p -> euclideanDistance(testPoint, p)));

        // Lấy k hàng xóm gần nhất
        List<String> nearestLabels = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            nearestLabels.add(nearestNeighbors.get(i).label);
        }

        // Trả về nhãn xuất hiện nhiều nhất
        return findMostFrequentLabel(nearestLabels);
    }

    // Hàm đọc dữ liệu từ file .txt
public static List<DataPoint> loadData(String fileName) throws IOException {
    List<DataPoint> dataPoints = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    String line;
    while ((line = reader.readLine()) != null) {
        String[] parts = line.trim().split("\\s+");
        
        // Kiểm tra số lượng phần tử trong mảng parts
        if (parts.length < 3) {
            System.err.println("Dòng dữ liệu không hợp lệ: " + line);
            continue; // Bỏ qua dòng dữ liệu không hợp lệ
        }
        
        try {
            double x1 = Double.parseDouble(parts[0]);
            double x2 = Double.parseDouble(parts[1]);
            String label = parts[2];  // "A", "B" hoặc "?" cho dữ liệu kiểm thử
            dataPoints.add(new DataPoint(x1, x2, label));
        } catch (NumberFormatException e) {
            System.err.println("Dữ liệu không hợp lệ: " + line);
        }
    }
    reader.close();
    return dataPoints;
}


    // Hàm tính độ chính xác
    public static double calculateAccuracy(List<DataPoint> testData, List<DataPoint> trainData, int k) {
        int correctPredictions = 0;
        int totalTests = 0;

        for (DataPoint testPoint : testData) {
            if (!testPoint.label.equals("?")) {
                // Dự đoán nhãn của điểm kiểm thử
                String predictedLabel = classify(testPoint, trainData, k);
                
                // So sánh dự đoán với nhãn thực tế
                if (predictedLabel.equals(testPoint.label)) {
                    correctPredictions++;
                }
                totalTests++;
            }
        }
        return totalTests > 0 ? (double) correctPredictions / totalTests : 0;
    }

    // Hàm chính
    public static void main(String[] args) {
        try {
            // Đọc dữ liệu huấn luyện từ file
            List<DataPoint> trainData = loadData("Train_data.txt");

            // Đọc dữ liệu kiểm thử từ file
            List<DataPoint> testData = loadData("Test_data.txt");

            // Giá trị k
            int k = 1;

            // Tạo file để ghi kết quả phân loại
            PrintWriter writer = new PrintWriter(new FileWriter("classification_results.txt"));

            // Phân loại từng điểm trong tập kiểm thử và tính độ chính xác
            int correctPredictions = 0;
            int totalTests = 0;

            // In tiêu đề cho kết quả phân loại
        System.out.println("Ket qua phan loai diem kiem thu:");
        for (DataPoint testPoint : testData) {
            if (testPoint.label.equals("?")) {
                // Phân loại điểm kiểm thử không có nhãn thực tế
                String predictedLabel = classify(testPoint, trainData, k);
                System.out.println("Diem kiem thu (" + testPoint.x1 + ", " + testPoint.x2 + ") duoc du doan thuoc lop: " + predictedLabel);
            } else {
                // Dự đoán cho các điểm kiểm thử có nhãn thực tế
                String predictedLabel = classify(testPoint, trainData, k);
                System.out.println("Diem kiem thu (" + testPoint.x1 + ", " + testPoint.x2 + ") co nhan thuc te: " + testPoint.label + ", duoc du doan thuoc lop: " + predictedLabel);
                if (predictedLabel.equals(testPoint.label)) {
                    correctPredictions++;
                }
                totalTests++;
            }
        }

            // Tính độ chính xác
            double accuracy = (totalTests > 0) ? (double) correctPredictions / totalTests : 0;
            System.out.println("Do chinh xac = " + (accuracy * 100) + "%");

            // Ghi độ chính xác vào file
            writer.println("Do chinh xac: " + (accuracy * 100) + "%");
            writer.close();

        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file: " + e.getMessage());
        }
    }
}
