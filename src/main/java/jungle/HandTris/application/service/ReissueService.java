package jungle.HandTris.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jungle.HandTris.presentation.dto.response.ReissueTokenRes;

public interface ReissueService {
    ReissueTokenRes reissue (HttpServletRequest request, String requestUsername);
}
