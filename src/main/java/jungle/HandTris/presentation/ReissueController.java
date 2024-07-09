package jungle.HandTris.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jungle.HandTris.application.service.ReissueService;
import jungle.HandTris.global.dto.ResponseEnvelope;
import jungle.HandTris.presentation.dto.response.ReissueTokenRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEnvelope<String> reissue (HttpServletRequest request) {
        String newAccessToken = reissueService.reissue(request);
    public ResponseEnvelope<ReissueTokenRes> reissue (HttpServletRequest request) {

        return ResponseEnvelope.of(newAccessToken);
    }
}
