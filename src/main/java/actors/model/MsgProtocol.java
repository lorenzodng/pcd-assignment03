package actors.model;

import actors.view.BoidView;

public interface MsgProtocol {
	record StartMsg(BoidView view, int nBoids) {}
	record StopMsg () {}
	record ResetMsg (BoidManager boidManager) {}
	record VelocityMsg() {}
	record VelocityDoneMsg() {}
	record PositionMsg() {}
	record PositionDoneMsg() {}

}
