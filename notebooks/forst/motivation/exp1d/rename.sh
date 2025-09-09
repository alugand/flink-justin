#!/bin/bash

# Ce script renomme tous les fichiers commençant par "exp2c"
# pour qu'ils commencent par "exp2b" à la place.

# Boucle sur chaque fichier correspondant au modèle "exp2c*"
for file in exp1a*
do
  # Vérifie si le fichier existe pour éviter les erreurs si aucun fichier ne correspond
  if [ -f "$file" ]; then
    # Construit le nouveau nom en remplaçant "exp2c" par "exp2b"
    new_name="exp1d${file#exp1a}"
    
    # Renomme le fichier
    mv "$file" "$new_name"
    
    # Affiche l'opération effectuée
    echo "Renommé '$file' en '$new_name'"
  fi
done

echo "Opération terminée."
