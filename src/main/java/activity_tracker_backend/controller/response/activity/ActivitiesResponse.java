package activity_tracker_backend.controller.response.activity;

import activity_tracker_backend.controller.response.Response;
import activity_tracker_backend.service.dto.ActivityReturn;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivitiesResponse extends Response {

    private List<ActivityReturn> activities;
}
