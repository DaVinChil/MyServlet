package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private PostController controller;
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    private static final String URL_API_POSTS = "/api/posts";
    private static final String URL_API_POSTS_ID = "/api/posts/\\d+";

    private static final String DELIMITER = "/";

    @Override
    public void init() {
        final var ctx = new AnnotationConfigApplicationContext("ru.netology");
        controller = ctx.getBean(PostController.class);
    }

    public boolean getAllPosts(String method, String path, HttpServletResponse resp) throws IOException {
        if (path.equals(URL_API_POSTS) && method.equals(GET)) {
            controller.all(resp);
            return true;
        }

        return false;
    }

    public boolean savePost(String method, String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (path.equals(URL_API_POSTS) && method.equals(POST)) {
            controller.save(req.getReader(), resp);
            return true;
        }

        return false;
    }

    public boolean getPost(String method, String path, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (path.matches(URL_API_POSTS_ID) && method.equals(GET)) {
            Long id = Long.parseLong(req.getRequestURI().substring(req.getRequestURI().lastIndexOf(DELIMITER)+1));
            controller.getById(id, resp);
            return true;
        }

        return false;
    }

    public boolean deletePost(String method, String path, HttpServletRequest req, HttpServletResponse resp){
        if (path.matches(URL_API_POSTS_ID) && method.equals(DELETE)) {
            Long id = Long.parseLong(req.getRequestURI().substring(req.getRequestURI().lastIndexOf(DELIMITER)+1));
            controller.removeById(id, resp);
            return true;
        }

        return false;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            // primitive routing
            if(getAllPosts(method, path, resp)){}
            else if(savePost(method, path, req, resp)){}
            else if(getPost(method, path, req, resp)){}
            else if(deletePost(method, path, req, resp)){}
            else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

