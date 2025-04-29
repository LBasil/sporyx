extends Control

func update_response(response):
    $VBoxContainer/ResponseLabel.text = "Réponse du serveur : " + response