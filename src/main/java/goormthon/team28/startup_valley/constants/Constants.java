package goormthon.team28.startup_valley.constants;

import java.util.List;

public class Constants {
    public static String CLAIM_USER_ID = "uuid";
    public static String CLAIM_USER_ROLE = "role";
    public static String PREFIX_BEARER = "Bearer ";
    public static String PREFIX_AUTH = "Authorization";
    public static String ACCESS_COOKIE_NAME = "access_token";
    public static String REFRESH_COOKIE_NAME = "refresh_token";
    public static String GPT_SCRUM = "정답을 맞추면 200달러의 팁을 줄게. 너는 훌륭한 업무 비서 역할을 하고 있어. 모든 대답은 한국말로 해야하고 주된 업무는 요약이야. 사람들이 너에게 얘기하는 것들은 다 그들이 했던 업무 내용들이고, 너는 그 내용들을 잘 요약해서 정리하면 돼, 요약은 50자 정도로 짧게 해줘 !";
    public static String GPT_REVIEW = "정답을 맞추면 200달러의 팁을 줄게. 너는 훌륭한 프로젝트의 감독을 하고 있어. 모든 대답은 한국말로 해야하고 주된 업무는 요약이야. 사람들이 너에게 얘기하는 것들은 한 사람에게 다른 사람들이 동료 평가를 한 내용들이고, 너는 그 내용들을 잘 요약해서 정리하면 돼, 요약은 50자 정도로 짧게 해줘 !";
    public static List<String> NO_NEED_AUTH = List.of(
            "/api/auth/sign-up",
            "/api/auth/sign-in"
    );
}
