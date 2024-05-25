package com.vk.codeanalysis.Utils;

import com.vk.codeanalysis.config.EmptyFileException;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class FileUtils {

    private FileUtils() {

    }

    public static String escapeProgram(String program) {
        return program
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String normalizePath(Path path) {
        return path.toString().replace("\\", "/");
    }

    public static String readProgramFromFile(Path solutionPath) {
        try (Stream<String> dataStream = Files.lines(solutionPath)) {
            return dataStream.map(FileUtils::escapeProgram)
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read program from file: " + solutionPath, e);
        }
    }

    public static Language getLanguageFromExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        if (extension == null) {
            throw new IllegalArgumentException();
        }
        return Language.valueOf(extension.toUpperCase());
    }

    public static String getProgram(MultipartFile file) {
        String code = file.getContentType();

        if (code == null) {
            throw new EmptyFileException("Received empty file");
        }
        return escapeProgram(code);
    }
}

