package snippet;

public class Snippet {
	 public void play(String fileName) {
	        try {
	            // MP3 파일을 읽기 위한 스트림 생성
	            FileInputStream fis = new FileInputStream(fileName);
	            Player player = new Player(fis); // JLayer의 Player 객체 생성
	            System.out.println(fileName + " is playing...");
	            
	            // MP3 재생 시작
	            player.play();
	        } catch (FileNotFoundException e) {
	            System.out.println("File not found: " + fileName);
	            e.printStackTrace();
	        } catch (Exception e) {
	            System.out.println("Error playing the file.");
	            e.printStackTrace();
	        }
	    }
}

