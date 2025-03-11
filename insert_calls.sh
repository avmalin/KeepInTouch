#!/bin/bash

# func to insert call to calllog
add_call() {
    NUMBER=$1
    TYPE=$2  #1in 2 out 3 miss
    DAYS_AGO=$3
    SECONDS_AGO=$((DAYS_AGO * 86400))
    TIMESTAMP=$(($(date +%s) - SECONDS_AGO))
    adb shell content insert --uri content://call_log/calls \
        --bind number:s:"$NUMBER" \
        --bind type:i:$TYPE \
        --bind date:l:$((TIMESTAMP * 1000)) \
        --bind duration:i:$((RANDOM % 300))
}

NUMBERS=("0501234567" "0522345678" "0533456789" "0544567890" "0555678901" "0566789012" "0577890123" "0588901234")
DAYS_OPTIONS=(2 7 14 30 46 60 90 134)


for i in "${!NUMBERS[@]}"; do
    NUMBER="${NUMBERS[$i]}"
    DAYS_AGO="${DAYS_OPTIONS[$i]}"
    for j in {1..20}; do  
        TYPE=$((RANDOM % 3 + 1))  
        add_call "$NUMBER" "$TYPE" "$DAYS_AGO"
        DAYS_AGO=$((DAYS_AGO + (RANDOM % 5 + 1)))  
    done
done

echo "insert call secsesfuly"
