extends Node2D

var inventory_ui = null
var explore_ui = null
var arena_ui = null
@onready var ui_container = $UI

func _ready():
	print("Initializing UI...")
	if ResourceLoader.exists("res://Inventory.tscn"):
		inventory_ui = load("res://Inventory.tscn").instantiate()
		ui_container.add_child(inventory_ui)
		inventory_ui.visible = false
		print("Inventory UI loaded")
	if ResourceLoader.exists("res://Explore.tscn"):
		explore_ui = load("res://Explore.tscn").instantiate()
		ui_container.add_child(explore_ui)
		explore_ui.visible = false
		print("Explore UI loaded")
	if ResourceLoader.exists("res://Arena.tscn"):
		arena_ui = load("res://Arena.tscn").instantiate()
		ui_container.add_child(arena_ui)
		arena_ui.visible = false
		print("Arena UI loaded")
	
	$UI/InventoryButton.pressed.connect(_on_inventory_button_pressed)
	$UI/ExploreButton.pressed.connect(_on_explore_button_pressed)
	$UI/ArenaButton.pressed.connect(_on_arena_button_pressed)

func _on_inventory_button_pressed():
	if inventory_ui:
		inventory_ui.visible = !inventory_ui.visible
		if explore_ui: explore_ui.visible = false
		if arena_ui: arena_ui.visible = false

func _on_explore_button_pressed():
	if explore_ui:
		explore_ui.visible = !explore_ui.visible
		if inventory_ui: inventory_ui.visible = false
		if arena_ui: arena_ui.visible = false

func _on_arena_button_pressed():
	if arena_ui:
		arena_ui.visible = !arena_ui.visible
		if inventory_ui: inventory_ui.visible = false
		if explore_ui: explore_ui.visible = false
		# Envoyer une requête "ping" au serveur
		send_ping_request()

func send_ping_request():
	var http_request = HTTPRequest.new()
	add_child(http_request)
	http_request.request_completed.connect(_on_request_completed)
	
	# Envoyer une requête GET au serveur (localhost:8080)
	var error = http_request.request("http://localhost:8080/ping")
	if error != OK:
		print("Error sending request: ", error)
	else:
		print("Request sent successfully")

func _on_request_completed(result, response_code, headers, body):
	print("Request completed with result: ", result)
	print("Response code: ", response_code)
	print("Headers: ", headers)
	print("Body (raw): ", body)
	if response_code == 200:
		var response = body.get_string_from_utf8()
		print("Server response (parsed): ", response)
		# Mettre à jour l'interface Arène avec la réponse
		if arena_ui and arena_ui.visible:
			arena_ui.update_response(response)
	else:
		print("Request failed with code: ", response_code)
