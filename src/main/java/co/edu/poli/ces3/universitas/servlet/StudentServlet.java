package co.edu.poli.ces3.universitas.servlet;

import co.edu.poli.ces3.universitas.model.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

@WebServlet(name = "studentServlet", value = "/student")
public class StudentServlet extends MyServlet {
    private String message;

    private ArrayList<Student> students;

    private GsonBuilder gsonBuilder;

    private Gson gson;


    public void init() {

        students = new ArrayList<>();
        Student student1 = new Student();
        student1.id = 1;
        student1.setName("Raul");
        student1.setDocument("1005678900");
        students.add(student1);
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        for (int i = 0; i < students.size(); i ++){
            System.out.println(students.get(i));
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        String studentID = request.getParameter("studentId");
        PrintWriter out = response.getWriter();
        if (studentID == null) {
            out.println(gson.toJson(students));
        } else {
            Student foundStudent = null;
            for (Student student : students) {
                if (student.getId() == Integer.parseInt(studentID)) {
                    foundStudent = student;
                    break;
                }
            }
            if (foundStudent != null) {
                out.println(gson.toJson(foundStudent));
            } else {
                out.println("El estudiante consultado no existe. Por favor valide la información.");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        resp.setContentType("application/json");
        JsonObject body =  this.getParamsFromPost(req);
        Student std = new Student(
                body.get("id").getAsInt(),
                body.get("document").getAsString(),
                body.get("name").getAsString()
        );
       this.students.add(std);
       out.println(gson.toJson(std));
        out.println("<b>Hello from post method</b>");
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String studentID = request.getParameter("studentId");
        PrintWriter out = response.getWriter();

        if (studentID == null) {
            out.println("El Id del estudiante es obligatorio.");
        } else {
            JsonObject body = this.getParamsFromBody(request);
            Student updatedStudent = null;
            for (Student student : students) {
                if (student.getId() == Integer.parseInt(studentID)) {
                    student.setDocument(body.get("document").getAsString());
                    student.setName(body.get("name").getAsString());
                    updatedStudent = student;
                    break;
                }
            }
            if (updatedStudent != null) {
                out.println(gson.toJson(updatedStudent));
            } else {
                out.println("El estudiante indicado no existe. Por favor valide la información.");
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String studentID = request.getParameter("studentId");
        PrintWriter out = response.getWriter();

        if (studentID == null) {
            out.println("El Id del estudiante es obligatorio.");
        } else {
            boolean studentRemoved = false;
            Iterator<Student> iterator = students.iterator();
            while (iterator.hasNext()) {
                Student student = iterator.next();
                if (student.getId() == Integer.parseInt(studentID)) {
                    iterator.remove();
                    studentRemoved = true;
                    out.println("El Estudiante fue eliminado con exito.");
                    break;
                }
            }
            if (!studentRemoved) {
                out.println("El estudiante indicado no existe. Por favor valide la información.");
            }
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response, JsonObject requestBody) throws ServletException, IOException {
        response.setContentType("application/json");
        String studentID = request.getParameter("studentId");
        PrintWriter out = response.getWriter();

        if (studentID == null) {
            out.println("El Id del estudiante es obligatorio.");
        } else {
            JsonObject body = this.getParamsFromBody(request);
            Student updatedStudent = null;
            for (Student student : students) {
                if (student.getId() == Integer.parseInt(studentID)) {
                    if (body.has("document")) {
                        student.setDocument(body.get("document").getAsString());
                    }
                    if (body.has("name")) {
                        student.setName(body.get("name").getAsString());
                    }
                    updatedStudent = student;
                    break;
                }
            }
            if (updatedStudent != null) {
                out.println(gson.toJson(updatedStudent));
            } else {
                out.println("El estudiante indicado no existe. Por favor valide la información.");
            }
        }
    }

    private JsonObject getParamsFromBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return gson.fromJson(buffer.toString(), JsonObject.class);
    }
}