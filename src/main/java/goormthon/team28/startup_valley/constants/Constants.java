package goormthon.team28.startup_valley.constants;

import java.util.List;

public class Constants {
    public static String CLAIM_USER_ID = "uuid";
    public static String CLAIM_USER_ROLE = "role";
    public static String PREFIX_BEARER = "Bearer ";
    public static String PREFIX_AUTH = "Authorization";
    public static String ACCESS_COOKIE_NAME = "access_token";
    public static String REFRESH_COOKIE_NAME = "refresh_token";
    public static String DISCORD_CONNECT_COMPLETE = "팀 과 팀 멤버를 연결했어요! '파트입력하기' 명령어를 통해 역할을 알려주세요 !!";
    public static String DISCORD_CONNECT_FAIL = "웹에 회원가입이 필요합니다!\n\n" + "회원가입 해주세요 !! : ";
    public static String DISCORD_INSERT_PART_COMPLETE = " 파트 입력까지 완료 되었습니다 !, 앞으로의 멋진 협업을 기대합니다 ! ";
    public static String DISCORD_INSERT_PART_FAIL = "파트가 제대로 입력되지 않았어요 ㅠㅠ \n\n 'BACKEND', 'FRONTEND', 'FULLSTACK', 'PM', 'DESIGN' 에서 입력해주세요 !";
    public static String DISCORD_INSERT_PART_YET = "님이 역할을 아직 입력하지 않으셨어요 ㅠㅠ 역할 입력 해주세요 ~ !";
    public static String DISCORD_REGISTER_QUESTION_COMPLETE = "질문이 등록 되었습니다 ! ";
    public static String DISCORD_INSERT_CODE_FAIL = "잘못된 코드 입니다, 코드를 확인해주세요 ~ !";
    public static String DISCORD_REGISTER_ANSWER_COMPLETE = "답변이 등록 되었습니다 ! ";
    public static String DISCORD_REGISTER_SCRUM_FAIL ="이전의 업무가 존재합니다 ㅠㅠ, 기존 업무를 종료하고 새로운 작업을 시작해주세요 ! ";
    public static String DISCORD_NO_PROCESSING_WORK = "진행 중인 작업이 없습니다 ! 업무 시작을 먼저 진행해주세요 ~ ! ";
    public static String DISCORD_NO_PROCESSING_SCRUM = "진행 했던 업무가 없어서 스크럼을 종료할 수 없습니다 ㅠㅠ.. 업무를 시작하여 데이터를 만들어 주세요 !!! ";
    public static String DISCORD_PLZ_MAKE_WORK_DONE ="진행 중인 업무가 존재합니다 ㅠㅠ 업무를 종료한 뒤, 스크럼을 종료하여 주세요 ! ";
    public static String DISCORD_GPT_WRONG = "gpt 요약 기능에서 문제가 생겼어요.. 잠시후 다시 시도해주세요 !";
    public static String DISCORD_REGISTER_SCRUM_COMPLETE ="하나의 스크럼이 마무리 됐어요 ~! 앞으로의 스크럼도 화이팅입니다 ~ !\n";
    public static String DISCORD_ONLY_LEADER_CAN = "프로젝트의 리더가 프로젝트의 상태를 변경할 수 있어요 ! \n\n";
    public static String DISCORD_YOU_ARENT_LEADER = "님은 프로젝트의 리더가 아닙니다..ㅠㅠ";
    public static String DISCORD_CHANGE_PROJECT_STATUS_COMPLETE = "프로젝트의 상태가 변경 되었습니다 !";
    public static String DISCORD_ANNOUNCE_EVERYONE = "@everyone ! 프로젝트는 잘 마무리 되었나요 ?! \n\n" +
            "프로젝트의 개발 기간이 끝나고 동료평가 단계로 넘어가게 되었습니다 ! \n\n" +
            "서로의 평가를 통해 한층 더 성장하세요 !!";
    public static String DISCORD_INFO_SAME = "이름과 이미지가 모두 동일합니다 ㅠㅠ";
    public static String DISCORD_INFO_CHANGE_COMPLETE = "서버의 정보가 변경되었습니다 ! 웹으로 확인해주세요 ~";
    public static String DISCORD_PLZ_UPDATE_TEAM1 = "팀원업데이트를 통해 팀을 만들어주세요 !";
    public static String DISCORD_PLZ_SIGNUP = "웹에 회원가입을 먼저 진행해주세요 !";
    public static String DISCORD_PLZ_UPDATE_TEAM2 = "팀원을 조회할 수 없습니다 ㅠㅠ. 팀원 업데이트를 통해 변경 사항을 적용해주세요 ~ !";
    public static String GPT_SCRUM = "정답을 맞추면 200달러의 팁을 줄게. 너는 훌륭한 업무 비서 역할을 하고 있어. 모든 대답은 한국말로 해야하고 주된 업무는 요약이야. 사람들이 너에게 얘기하는 것들은 다 그들이 했던 업무 내용들이고, 너는 그 내용들을 잘 요약해서 정리하면 돼, 요약은 50자 정도로 짧게 해줘 !";
    public static String GPT_REVIEW = "정답을 맞추면 200달러의 팁을 줄게. 너는 훌륭한 프로젝트의 감독을 하고 있어. 모든 대답은 한국말로 해야하고 주된 업무는 요약이야. 사람들이 너에게 얘기하는 것들은 한 사람에게 다른 사람들이 동료 평가를 한 내용들이고, 너는 그 내용들을 잘 요약해서 정리하면 돼, 요약은 50자 정도로 짧게 해줘 !";
    public static List<String> NO_NEED_AUTH = List.of(
            "/api/auth/sign-up",
            "/api/auth/sign-in",
            "/login/oauth2/code/discord"
    );
}
