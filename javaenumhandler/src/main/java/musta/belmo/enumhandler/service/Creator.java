package musta.belmo.enumhandler.service;


import com.github.javaparser.ast.CompilationUnit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

public class Creator {
    public static void main(String[] args) throws Exception {
        SpringBootServiceApiGenerator apiGenerator = new SpringBootServiceApiGenerator();
        SpringBootServiceGenerator springBootServiceGenerator = new SpringBootServiceGenerator();
        SpringControllerGenerator springControllerGenerator = new SpringControllerGenerator();
        RepostioryController repostioryController = new RepostioryController();

        String modelPath = "C:\\Users\\mustapha\\Desktop\\javaProjects\\GestionParkAuto\\gestion-parc-auto\\gestion-parc-auto-backend\\src\\main\\java\\com\\gestparcauto\\backend\\model";
        String servicePath = "C:\\Users\\mustapha\\Desktop\\javaProjects\\GestionParkAuto\\gestion-parc-auto\\gestion-parc-auto-backend\\src\\main\\java\\com\\gestparcauto\\backend\\service.impl";
        String apiPath = "C:\\Users\\mustapha\\Desktop\\javaProjects\\GestionParkAuto\\gestion-parc-auto\\gestion-parc-auto-backend\\src\\main\\java\\com\\gestparcauto\\backend\\service.api";
        String controllerPath = "C:\\Users\\mustapha\\Desktop\\javaProjects\\GestionParkAuto\\gestion-parc-auto\\gestion-parc-auto-backend\\src\\main\\java\\com\\gestparcauto\\backend\\controller";
        String repositoryPath = "C:\\Users\\mustapha\\Desktop\\javaProjects\\GestionParkAuto\\gestion-parc-auto\\gestion-parc-auto-backend\\src\\main\\java\\com\\gestparcauto\\backend\\repository";
        Collection<File> files = FileUtils.listFiles(
                new File(modelPath), new String[]{"java"}, true);

        for (File file : files) {
            String classCode = FileUtils.readFileToString(file, "UTF-8");
            CompilationUnit generateService = springBootServiceGenerator.generateService(classCode);
            CompilationUnit genrateApi = apiGenerator.generateService(classCode);
            String generateController = springControllerGenerator.generateService(classCode);
            String className = file.getName().replace(".java", "");
            String controllerRepository = repostioryController.createRepository(className);


          /*  FileUtils.write(new File(servicePath, className +
                    "ServiceImpl.java"), generateService.toString(), "UTF-8");


            FileUtils.write(new File(apiPath, className +
                    "Service.java"), genrateApi.toString(), "UTF-8");*/

            FileUtils.write(new File(controllerPath, className +
                    "Controller.java"), generateController, "UTF-8");

/*
            FileUtils.write(new File(repositoryPath, className +
                    "Repository.java"), controllerRepository, "UTF-8");
*/
            // FileUtils.write(new File(controllerPath), generateController, "UTF-8");
        }
    }
}
