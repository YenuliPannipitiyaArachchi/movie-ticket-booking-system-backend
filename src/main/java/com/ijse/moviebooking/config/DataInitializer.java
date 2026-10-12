package com.ijse.moviebooking.config;

import com.ijse.moviebooking.entity.*;
import com.ijse.moviebooking.enums.*;
import com.ijse.moviebooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/** Inserts demo data on the first start (disable with app.seed=false). */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final MovieRepository movies;
    private final TheatreRepository theatres;
    private final ShowRepository shows;
    private final PasswordEncoder encoder;

    @Value("${app.seed:true}")
    private boolean seed;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seed || users.count() > 0)
            return;

        user("Admin", "admin@movie.com", "Admin123", Role.ADMIN);
        user("Demo Customer", "customer@movie.com", "Customer123", Role.CUSTOMER);

        Movie m1 = movie("Midnight Reel", "Thriller", "English", 118,
                "A projectionist finds a film that predicts tomorrow.", "/demo-posters/midnight-reel.svg",
                MovieStatus.NOW_SHOWING);
        Movie m2 = movie("Monsoon Road", "Drama", "Sinhala", 105,
                "Two strangers share a long bus ride across the island.", "/demo-posters/monsoon-road.svg",
                MovieStatus.NOW_SHOWING);
        Movie m3 = movie("Stellar Drift", "Sci-Fi", "English", 132, "A crew of five faces a silent ship.",
                "/demo-posters/stellar-drift.svg", MovieStatus.NOW_SHOWING);
        movie("Coming Soon: Paper Lanterns", "Animation", "English", 96, "A festival night, a lost lantern.",
                "/demo-posters/paper-lanterns.svg", MovieStatus.UPCOMING);

        Theatre t1 = theatre("Majestic Cinema", "Colombo 03", 50);
        Theatre t2 = theatre("Galle Face Screens", "Colombo 01", 80);

        for (int d = 1; d <= 3; d++) {
            LocalDate date = LocalDate.now().plusDays(d);
            show(m1, t1, date, LocalTime.of(18, 30), "1200.00");
            show(m2, t1, date, LocalTime.of(21, 0), "1000.00");
            show(m3, t2, date, LocalTime.of(19, 0), "1500.00");
        }
        log.info(
                "Demo data created. Admin login: admin@movie.com / Admin123, customer: customer@movie.com / Customer123");
    }

    private void user(String name, String email, String pw, Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(pw));
        u.setRole(role);
        users.save(u);
    }

    private Movie movie(String title, String genre, String lang, int mins, String desc, String posterUrl,
            MovieStatus status) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setGenre(genre);
        m.setLanguage(lang);
        m.setDurationMinutes(mins);
        m.setDescription(desc);
        m.setPosterUrl(posterUrl);
        m.setStatus(status);
        m.setReleaseDate(LocalDate.now());
        return movies.save(m);
    }

    private Theatre theatre(String name, String location, int capacity) {
        Theatre t = new Theatre();
        t.setName(name);
        t.setLocation(location);
        t.setCapacity(capacity);
        t.setStatus(TheatreStatus.ACTIVE);
        return theatres.save(t);
    }

    private void show(Movie m, Theatre t, LocalDate date, LocalTime time, String price) {
        Show s = new Show();
        s.setMovie(m);
        s.setTheatre(t);
        s.setShowDate(date);
        s.setShowTime(time);
        s.setTicketPrice(new BigDecimal(price));
        s.setStatus(ShowStatus.SCHEDULED);
        shows.save(s);
    }
}
