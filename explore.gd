extends Control

var worlds = [
    {"name": "Forêt Mycélique", "description": "Une forêt luxuriante remplie de champignons géants et de spores flottantes."},
    {"name": "Caverne Sporique", "description": "Une grotte sombre où brillent des cristaux sporiques luminescents."}
]

var timer = 0.0
var timer_active = false

func _ready():
    for world in worlds:
        $Panel/VBoxContainer/WorldsList.add_item(world.name)
    $Panel/VBoxContainer/ExploreButton.pressed.connect(_on_explore_button_pressed)

func _process(delta):
    if timer_active:
        timer -= delta
        update_combat_log("Exploration en cours... Temps restant : " + str(int(timer)) + "s")

func _on_explore_button_pressed():
    var selected = $Panel/VBoxContainer/WorldsList.get_selected_items()
    if selected.size() > 0:
        var world_index = selected[0]
        if world_index < worlds.size():
            var world = worlds[world_index]
            $Panel/VBoxContainer/WorldsList.deselect_all()
            start_exploration(world.name)

func start_exploration(world_name):
    var http_request = HTTPRequest.new()
    add_child(http_request)
    http_request.request_completed.connect(_on_explore_request_completed)
    
    var body = JSON.stringify({"world": world_name})
    var error = http_request.request("http://127.0.0.1:8080/explore", ["Content-Type: application/json"], HTTPClient.METHOD_POST, body)
    if error != OK:
        update_combat_log("Erreur lors de la requête d'exploration : " + str(error))
    else:
        update_combat_log("Requête d'exploration envoyée pour " + world_name)
        timer = 10.0
        timer_active = true

func _on_explore_request_completed(result, response_code, headers, body):
    timer_active = false
    if response_code == 200:
        var response = body.get_string_from_utf8()
        print("Raw response: ", response) # Débogage
        update_combat_log(response)
    else:
        update_combat_log("Erreur serveur : " + str(response_code))

func update_combat_log(message):
    var lines = message.split("\n")
    var combat_log_lines = []
    for line in lines:
        if line.strip_edges() != "":
            combat_log_lines.append(line.strip_edges())
    if combat_log_lines.size() > 100:
        combat_log_lines = combat_log_lines.slice(combat_log_lines.size() - 100)
    $Panel/VBoxContainer/CombatLog.text = "[center]" + "\n".join(combat_log_lines) + "[/center]"
    $Panel/VBoxContainer/CombatLog.scroll_to_line($Panel/VBoxContainer/CombatLog.get_line_count() - 1)