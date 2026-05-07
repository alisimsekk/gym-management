package com.alisimsek.service.impl;

import com.alisimsek.dto.request.TrainerWorkloadRequest;
import com.alisimsek.dto.response.MonthlyWorkload;
import com.alisimsek.dto.response.TrainerWorkloadSummary;
import com.alisimsek.dto.response.YearlyWorkload;
import com.alisimsek.enums.ActionType;
import com.alisimsek.exception.customException.TrainerWorkloadNotFoundException;
import com.alisimsek.model.TrainerWorkload;
import com.alisimsek.repository.TrainerWorkloadRepository;
import com.alisimsek.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;

    @Override
    public void handleWorkload(TrainerWorkloadRequest workloadRequest) {
        log.info("Handling workload request: {}", workloadRequest);
        var trainingDate = workloadRequest.getTrainingDate();
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();

        TrainerWorkload trainerDoc = trainerWorkloadRepository
                .findByTrainerUsernameAndActive(workloadRequest.getTrainerUsername(), true)
                .orElseGet(() -> buildTrainerWorkload(workloadRequest));

        // find or create year node
        TrainerWorkload.YearlyWorkload yearNode = trainerDoc.getYearlyWorkloads().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    TrainerWorkload.YearlyWorkload yearlyWorkload = buildYearWorkload(year);
                    trainerDoc.getYearlyWorkloads().add(yearlyWorkload);
                    return yearlyWorkload;
                });

        // find or create month node
        TrainerWorkload.MonthlyWorkload monthNode = yearNode.getMonthlyWorkloads().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    TrainerWorkload.MonthlyWorkload monthlyWorkload = buildMonthWorkload(month);
                    yearNode.getMonthlyWorkloads().add(monthlyWorkload);
                    return monthlyWorkload;
                });

        int trainingDuration = workloadRequest.getTrainingDuration();
        int delta = ActionType.ADD.equals(workloadRequest.getActionType())
                ? trainingDuration : (-trainingDuration);

        int newMonthlyTotalTrainingDuration = NumberUtils.max(NumberUtils.INTEGER_ZERO, monthNode.getTotalTrainingDuration() + delta);
        monthNode.setTotalTrainingDuration(newMonthlyTotalTrainingDuration);
        trainerWorkloadRepository.save(trainerDoc);
        log.info("Workload successfully saved");
    }

    @Override
    public TrainerWorkloadSummary getWorkloadSummary(String trainerUsername) {
        log.info("Getting workload summary for trainer username: {}", trainerUsername);
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByTrainerUsernameAndActive(trainerUsername, true)
                .orElseThrow(TrainerWorkloadNotFoundException::new);

        List<YearlyWorkload> yearlyWorkloads = trainerWorkload.getYearlyWorkloads().stream()
                .filter(y -> !y.getMonthlyWorkloads().isEmpty())
                .map(y -> {
                    List<MonthlyWorkload> monthlyWorkloads = y.getMonthlyWorkloads().stream()
                            .filter(m -> m.getTotalTrainingDuration() > 0)
                            .map(m -> MonthlyWorkload.builder()
                                    .month(Month.of(m.getMonth()))
                                    .totalTrainingDuration(m.getTotalTrainingDuration())
                                    .build())
                            .sorted(Comparator.comparing(MonthlyWorkload::month))
                            .collect(Collectors.toList());
                    return YearlyWorkload.builder()
                            .year(y.getYear())
                            .monthlyWorkloads(monthlyWorkloads)
                            .build();
                })
                .filter(y -> !y.monthlyWorkloads().isEmpty())
                .sorted((y1, y2) -> y2.year().compareTo(y1.year()))
                .collect(Collectors.toList());

        return TrainerWorkloadSummary.builder()
                .trainerUsername(trainerWorkload.getTrainerUsername())
                .trainerFirstName(trainerWorkload.getTrainerFirstName())
                .trainerLastName(trainerWorkload.getTrainerLastName())
                .active(trainerWorkload.isActive())
                .yearlyWorkloads(yearlyWorkloads)
                .build();
    }

    @Override
    public Optional<TrainerWorkload> getTrainerWorkloadByUsername(String trainerUsername) {
        return trainerWorkloadRepository.findByTrainerUsernameAndActive(trainerUsername, true);
    }

    @Override
    public void updateWorkload(TrainerWorkload trainerWorkload) {
        trainerWorkloadRepository.save(trainerWorkload);
    }

    private TrainerWorkload.MonthlyWorkload buildMonthWorkload(int month) {
        return TrainerWorkload.MonthlyWorkload.builder()
                .month(month)
                .totalTrainingDuration(0)
                .build();
    }

    private TrainerWorkload.YearlyWorkload buildYearWorkload(int year) {
        return TrainerWorkload.YearlyWorkload.builder()
                .year(year)
                .monthlyWorkloads(new ArrayList<>())
                .build();
    }

    private TrainerWorkload buildTrainerWorkload(TrainerWorkloadRequest workloadRequest) {
        TrainerWorkload doc = new TrainerWorkload();
        doc.setTrainerUsername(workloadRequest.getTrainerUsername());
        doc.setTrainerFirstName(workloadRequest.getTrainerFirstName());
        doc.setTrainerLastName(workloadRequest.getTrainerLastName());
        doc.setActive(workloadRequest.isActive());
        return doc;
    }
}
