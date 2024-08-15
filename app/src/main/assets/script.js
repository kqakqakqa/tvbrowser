function removeElements(t) {
    let exist = document.querySelector("*:not(:has(#player),#player *,#player,script,:has(script))") && document.querySelector("#pic_in_pic_player, #contextmenu_player, #control_bar_player");
    if (exist) {
        document.querySelectorAll("*:not(:has(#player),#player *,#player,script,:has(script))").forEach(e => { e.remove(); });
        document.querySelectorAll("#pic_in_pic_player, #contextmenu_player, #control_bar_player").forEach(e => { e.remove(); });
        t = 500;
    }
    else { t = Math.min(t * 2, 4000); }
    setTimeout(() => { removeElements(t) }, t);
}
removeElements(500);

function applyStyles(t) {
    let exist = document.querySelector("#player") && document.querySelector("body");
    if (exist) {
        let applied = document.querySelector("body").style.getPropertyValue("margin") == "0px" &&
            document.querySelector("body").style.getPropertyValue("background-color") == "black" &&
            document.querySelector("#player").style.getPropertyValue("height") == "100vh" &&
            document.querySelector("#player").style.getPropertyValue("width") == "100vw";
        if (!applied) {
            document.querySelector("body").style.cssText = "margin: 0; background-color: black;";
            document.querySelector("#player").style.cssText = "height: 100vh!important; width: 100vw!important;";
        }
        t = 4000;
    }
    setTimeout(() => { applyStyles(t) }, t);
}
applyStyles(500);