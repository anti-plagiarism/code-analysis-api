package com.vk.codeanalysis.core.distributor;

import com.vk.codeanalysis.core.report_generator.ReportGeneratorServiceImpl;
import com.vk.codeanalysis.public_interface.tokenizer.TaskCollectorV1;
import com.vk.codeanalysis.public_interface.tokenizer.Language;
import com.vk.codeanalysis.public_interface.distributor.DistributorServiceV0;
import com.vk.codeanalysis.public_interface.dto.SolutionPutRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributorServiceImpl implements DistributorServiceV0 {
    private final ExecutorService executor;
    private final Map<Language, TaskCollectorV1> collectors;
    private final ReportGeneratorServiceImpl reportGenerator;

    @Override
    public void put(SolutionPutRequest request) {
        TaskCollectorV1 collector = collectors.get(request.lang());

        if (collector == null) {
            throw new IllegalArgumentException("Unsupported language");
        }

        executor.execute(() ->
                collector.add(request.taskId(), request.solutionId(), request.program())
        );
    }

    @Override
    public String getReport(float thresholdStart, float thresholdEnd) {

        if (
                thresholdStart < 0 || thresholdStart > 100
                || thresholdEnd < 0 || thresholdEnd > 100
                || thresholdStart > thresholdEnd
        ) {
            throw new IllegalArgumentException("Wrong similarity threshold value");
        }

        return reportGenerator.generate(thresholdStart, thresholdEnd);
    }
}
