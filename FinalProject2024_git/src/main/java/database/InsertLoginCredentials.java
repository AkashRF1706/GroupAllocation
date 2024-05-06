package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;


public class InsertLoginCredentials {
	
	public static void main(String[] args) throws SQLException {
		
		Connection connection = MySQLConnection.getConnection();
		
		System.out.println("Enter which table to update:");
		System.out.println("1. Students");
		System.out.println("2. Staffs");
		System.out.println("3. Login");
		
		Scanner scanner = new Scanner(System.in);
		int option = scanner.nextInt();
		
		switch (option) {
		case 1: {
			insertStudent(connection);
			break;
		}
		case 2: {
			insertStaff(connection);
			break;
		}
		case 3: {
			insertLogin(connection);
			break;
		}
		case 4: {
			insertTopic(connection);
			break;
		}
		default:
			System.out.println("Enter the correct option");
			break;
		}		
	}
	
	public static void insertStudent (Connection conn) {
		String[][] studentData = {
				{"John Doe", "345678901", "jd8@student.le.ac.uk", "jd8", "Computer Science"},
			    {"Emily Smith", "456789012", "es9@student.le.ac.uk", "es9", "Computer Science"},
			    {"Sarah Johnson", "567890123", "sj7@student.le.ac.uk", "sj7", "Computer Science"},
			    {"David Williams", "678901234", "dw6@student.le.ac.uk", "dw6", "Computer Science"},
			    {"Jessica Brown", "789012345", "jb5@student.le.ac.uk", "jb5", "Computer Science"},
			    {"Andrew Garcia", "890123456", "ag4@student.le.ac.uk", "ag4", "Computer Science"},
			    {"Samantha Miller", "901234567", "sm3@student.le.ac.uk", "sm3", "Computer Science"},
			    {"Matthew Davis", "012345678", "md2@student.le.ac.uk", "md2", "Computer Science"},
			    {"Amanda Wilson", "987654321", "aw1@student.le.ac.uk", "aw1", "Computer Science"},
			    {"Christopher Martinez", "876543210", "cm0@student.le.ac.uk", "cm0", "Computer Science"},
			    {"Nicole Taylor", "765432109", "nt9@student.le.ac.uk", "nt9", "Computer Science"},
			    {"Benjamin Anderson", "654321098", "ba8@student.le.ac.uk", "ba8", "Computer Science"},
			    {"Elizabeth Thomas", "543210987", "et7@student.le.ac.uk", "et7", "Computer Science"},
			    {"Daniel Jackson", "432109876", "dj6@student.le.ac.uk", "dj6", "Computer Science"},
			    {"Ashley White", "321098765", "aw5@student.le.ac.uk", "aw5", "Computer Science"},
			    {"Kimberly Harris", "210987654", "kh4@student.le.ac.uk", "kh4", "Computer Science"},
			    {"Christopher Clark", "109876543", "cc3@student.le.ac.uk", "cc3", "Computer Science"},
			    {"Jennifer Martinez", "987654321", "jm2@student.le.ac.uk", "jm2", "Computer Science"},
			    {"Josephine Thomas", "876543210", "jt1@student.le.ac.uk", "jt1", "Computer Science"},
			    {"Alexander Jackson", "765432109", "aj0@student.le.ac.uk", "aj0", "Computer Science"},
			    {"Samantha White", "654321098", "sw9@student.le.ac.uk", "sw9", "Computer Science"},
			    {"Nicholas Harris", "543210987", "nh8@student.le.ac.uk", "nh8", "Computer Science"},
			    {"Victoria Clark", "432109876", "vc7@student.le.ac.uk", "vc7", "Computer Science"},
			    {"Patrick Martinez", "321098765", "pm6@student.le.ac.uk", "pm6", "Computer Science"},
			    {"Danielle Thomas", "210987654", "dt5@student.le.ac.uk", "dt5", "Computer Science"},
			    {"Jonathan Jackson", "109876543", "jj4@student.le.ac.uk", "jj4", "Computer Science"},
			    {"Stephanie White", "987654321", "sw3@student.le.ac.uk", "sw3", "Computer Science"},
			    {"Timothy Harris", "876543210", "th2@student.le.ac.uk", "th2", "Computer Science"}
		};
		
		try {
			String insertQuery = "INSERT INTO Students (student_name, student_number, email, username, department) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ptst = conn.prepareStatement(insertQuery);
            int insertCount = 0;
            
            for(String[] student : studentData) {
            	String studentName = student[0]; 
            	String studentNumber = student[1];
            	String email = student[2];
            	String username = student[3];
            	String department = student[4];
            	
            	ptst.setString(1, studentName);
            	ptst.setInt(2, Integer.parseInt(studentNumber));
            	ptst.setString(3, email);
            	ptst.setString(4, username);
            	ptst.setString(5, department);
            	
            	insertCount = ptst.executeUpdate();
            }
            
            if(insertCount > 0) {
            	System.out.println("Inserted Successfulyy....");
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void insertStaff (Connection conn) {
		String[][] staffData = {
				{"Alex Johnson", "aj101@leicester.ac.uk", "aj101", "Y", "staff", "Computer Science"},
				{"Brittany Smith", "bs202@leicester.ac.uk", "bs202", "Y", "staff", "Computer Science"},
				{"Charles Brown", "cb303@leicester.ac.uk", "cb303", "Y", "staff", "Computer Science"},
				{"Diana Garcia", "dg404@leicester.ac.uk", "dg404", "Y", "staff", "Computer Science"},
				{"Evan Miller", "em505@leicester.ac.uk", "em505", "Y", "staff", "Computer Science"},
				{"Fiona Davis", "fd606@leicester.ac.uk", "fd606", "Y", "staff", "Computer Science"},
				{"George Wilson", "gw707@leicester.ac.uk", "gw707", "Y", "staff", "Computer Science"},
				{"Hannah Martinez", "hm808@leicester.ac.uk", "hm808", "Y", "staff", "Computer Science"},
				{"Ian Taylor", "it909@leicester.ac.uk", "it909", "Y", "staff", "Computer Science"},
				{"Julia Anderson", "ja010@leicester.ac.uk", "ja010", "Y", "staff", "Computer Science"},
				{"Kevin Thomas", "kt111@leicester.ac.uk", "kt111", "Y", "staff", "Computer Science"},
				{"Laura Jackson", "lj212@leicester.ac.uk", "lj212", "Y", "staff", "Computer Science"},
				{"Michael White", "mw313@leicester.ac.uk", "mw313", "Y", "staff", "Computer Science"},
				{"Nora Harris", "nh414@leicester.ac.uk", "nh414", "Y", "staff", "Computer Science"},
				{"Olivia Clark", "oc515@leicester.ac.uk", "oc515", "Y", "staff", "Computer Science"}
		};
		
		try {
			String insertQuery = "INSERT INTO Staff (staff_name, email, username, is_supervisor, role, department) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ptst = conn.prepareStatement(insertQuery);
            int insertCount = 0;
            
            for(String[] staff : staffData) {
            	String staffName = staff[0]; 
            	String email = staff[1];
            	String username = staff[2];
            	String is_supervisor = staff[3];
            	String role = staff[4];
            	String department = staff[5];
            	
            	
            	ptst.setString(1, staffName);
            	ptst.setString(2, email);
            	ptst.setString(3, username);
            	ptst.setString(4, is_supervisor);
            	ptst.setString(5, role);
            	ptst.setString(6, department);
            	
            	insertCount = ptst.executeUpdate();
            }
            
            if(insertCount > 0) {
            	System.out.println("Inserted Successfulyy....");
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void insertLogin(Connection conn) {
		String[][] loginData = {//rar37 - Rosh00@
				    {"3", "19", "bm305", "Brendon@123", "A", "Active"},
				    			
		};
		
		try {
			
			String insertQuery = "INSERT INTO Login (student_id, staff_id, username, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ptst = conn.prepareStatement(insertQuery);
            int insertCount = 0;
            
            for(String[] user : loginData) {
            	String studentID = user[0]; 
            	String staffID = user[1];
            	String username = user[2];
            	String password = user[3];
            	String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            	String role = user[4];
            	String status = user[5];
            	
            	ptst.setInt(1, Integer.parseInt(studentID));
            	ptst.setInt(2, Integer.parseInt(staffID));
            	ptst.setString(3, username);
            	ptst.setString(4, hashedPassword);
            	ptst.setString(5, role);
            	ptst.setString(6, status);
            	
            	insertCount = ptst.executeUpdate();
            }
            
            if(insertCount > 0) {
            	System.out.println("Inserted Successfulyy....");
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void insertTopic(Connection conn) {
		String[][] topicData = {
				{"Cybersecurity Challenges in IoT"," Explore the security vulnerabilities in Internet of Things (IoT) devices and networks, and discuss strategies to mitigate risks.","Y","Computer Science"},
				{"Blockchain Technology", "Delve into the fundamentals of blockchain, its applications beyond cryptocurrencies, and the potential impact on various industries.", "Y", "Computer Science"},
				{"Cloud Computing Adoption", "Examine the benefits and challenges of migrating to cloud-based infrastructure, including security, scalability, and cost considerations.", "Y", "Computer Science"},
				{"Data Privacy Regulations", "Analyze the impact of data privacy laws such as GDPR and CCPA on businesses, and discuss compliance strategies and challenges.", "Y", "Computer Science"},
				{"Artificial Intelligence (AI) Ethics", "Discuss the ethical implications of AI technologies, including privacy concerns, algorithmic biases, and autonomous decision-making.", "Y", "Computer Science"},
				{"Quantum Computing", "Discuss the principles of quantum computing, its potential to revolutionize computational power, and current research challenges.", "Y", "Computer Science"},
				{"Big Data Analytics", "Explore the use of big data analytics in business intelligence, predictive modeling, and decision-making processes, and discuss privacy and ethical considerations.", "Y", "Computer Science"},
				{"Machine Learning Applications", "Explore real-world applications of machine learning, such as natural language processing, image recognition, and recommendation systems.", "Y", "Computer Science"},
				{"Open Source Software", "Discuss the benefits of open source software development, community collaboration, and the challenges of maintaining security and quality.", "Y", "Computer Science"},
				{"Internet Censorship and Freedom", "Debate the ethical and legal implications of internet censorship, including government surveillance, online privacy, and freedom of speech.", "Y", "Computer Science"},
				{"Software Development Methodologies", "Compare agile, waterfall, and DevOps methodologies, and discuss their strengths, weaknesses, and suitability for different projects.", "Y", "Computer Science"},
				{"Ethical Hacking and Penetration Testing", "Discuss the importance of ethical hacking in identifying and patching security vulnerabilities, and the ethical considerations involved.", "Y", "Computer Science"},
				{"Edge Computing", "Delve into the concept of edge computing, its advantages in latency-sensitive applications, and its implications for IoT and cloud computing.", "Y", "Computer Science"},
				{"5G Technology Impact", "Analyze the potential of 5G technology to transform industries such as telecommunications, healthcare, autonomous vehicles, and smart cities.", "Y", "Computer Science"},
				{"Human-Computer Interaction (HCI)", "Explore principles of HCI design, usability testing methods, and the role of user experience (UX) in software development.", "Y", "Computer Science"},
				{"Natural Language Processing (NLP)", "Discuss the applications of NLP in virtual assistants, chatbots, sentiment analysis, and language translation, and the challenges of language understanding.", "Y", "Computer Science"},
				{"Robotics and Automation", "Explore the impact of robotics and automation on various industries, including manufacturing, healthcare, agriculture, and logistics.", "Y", "Computer Science"},
				{"Ethical Considerations in AI Development", "Debate ethical dilemmas in AI research and development, including job displacement, algorithmic biases, and accountability for AI decisions.", "Y", "Computer Science"},
				{"Biometric Authentication Technologies", "Explore biometric authentication methods such as fingerprint recognition, facial recognition, and iris scanning, and discuss privacy concerns and security implications.", "Y", "Computer Science"},
				{"Augmented Reality (AR) and Virtual Reality (VR)", "Explore the applications of AR and VR technologies in gaming, education, healthcare, and other sectors, and discuss future trends.", "Y", "Computer Science"}
		};
		
		try {
			
			String insertQuery = "INSERT INTO Topics (topic_name, description, is_available, department) VALUES (?, ?, ?, ?)";
            PreparedStatement ptst = conn.prepareStatement(insertQuery);
            int insertCount = 0;
            
            for(String[] topic : topicData) {
            	String topicName = topic[0]; 
            	String description = topic[1];
            	String availability = topic[2];
            	String department = topic[3];
            	
            	ptst.setString(1, topicName);
            	ptst.setString(2, description);
            	ptst.setString(3, availability);
            	ptst.setString(4, department);
            	
            	insertCount = ptst.executeUpdate();
            }
            
            if(insertCount > 0) {
            	System.out.println("Inserted Successfulyy....");
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
