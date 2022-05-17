package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FilmorateApplicationTests {

	UserController userController = new UserController();

	@Test
	void contextLoads() {
	}

}