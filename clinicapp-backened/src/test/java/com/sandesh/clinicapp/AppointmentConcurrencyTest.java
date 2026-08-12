package com.sandesh.clinicapp;

import com.sandesh.clinicapp.dto.BookingRequest;
import com.sandesh.clinicapp.exception.SlotConflictException;
import com.sandesh.clinicapp.model.*;
import com.sandesh.clinicapp.repository.SlotRepository;
import com.sandesh.clinicapp.repository.UserRepository;
import com.sandesh.clinicapp.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AppointmentConcurrencyTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void onlyOnePatientShouldWinTheSameSlot() throws InterruptedException {

        User doctor = new User();
        doctor.setName("Dr. Concurrency");
        doctor.setEmail("concurrency.doctor@test.com");
        doctor.setPassword("test@12");
        doctor.setRole(Role.DOCTOR);
        userRepository.save(doctor);

        Slot slot = new Slot();
        slot.setDoctor(doctor);
        slot.setStartTime(java.time.LocalDateTime.now().plusDays(1));
        slot.setEndTime(java.time.LocalDateTime.now().plusDays(1).plusMinutes(30));
        slot.setStatus(SlotStatus.AVAILABLE);
        slotRepository.save(slot);

        int numPatients = 10;
        List<User> patients = new java.util.ArrayList<>();
        for (int i = 0; i < numPatients; i++) {
            User patient = new User();
            patient.setName("Patient " + i);
            patient.setEmail("patient" + i + "@test.com");
            patient.setPassword("test@123");
            patient.setRole(Role.PATIENT);
            userRepository.save(patient);
            patients.add(patient);
        }


        ExecutorService executor = Executors.newFixedThreadPool(numPatients);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        List<Future<?>> futures = new java.util.ArrayList<>();

        for (User patient : patients) {
            futures.add(executor.submit(() -> {
                try {
                    startLatch.await();
                    BookingRequest request = new BookingRequest();
                    request.setSlotId(slot.getId());
                    appointmentService.bookAppointment(patient.getEmail(), request);
                    successCount.incrementAndGet();
                } catch (SlotConflictException e) {
                    conflictCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }

        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);


        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(numPatients - 1, conflictCount.get(), "All others should get a conflict");

        System.out.println("Successes: " + successCount.get() + ", Conflicts: " + conflictCount.get());
    }
}