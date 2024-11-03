import Utils from "./util/utils.js";
import { Controller as VestibuleController } from "./vestibule/controller.js";

new VestibuleController(new Utils(), document.location);
