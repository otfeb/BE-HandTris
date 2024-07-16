package jungle.HandTris.presentation.dto.request;

public record TetrisMessageReq(String[][] board, boolean isEnd, boolean isAddAttack, boolean isFlipAttack,
                               boolean isDonutAttack) {
}
