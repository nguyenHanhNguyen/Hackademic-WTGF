from random import randrange
import datetime
from django.http import HttpResponse
from models import Category, Workout, User
import json
from calendar import monthrange, Calendar


def categories(request):
    category_list = Category.objects.all()
    results = [ob.as_json() for ob in category_list]
    return HttpResponse(json.dumps(results), content_type="application/json")


def category(request, category_id):
    the_category = Category.objects.get(pk=category_id)
    workouts = Workout.objects.filter(category=the_category)
    workout_results = [ob.as_json() for ob in workouts]
    return HttpResponse(json.dumps(workout_results), content_type="application/json")


def workout(request, workout_id):
    the_workout = Workout.objects.get(pk=workout_id).as_json()
    return HttpResponse(json.dumps(the_workout), content_type="application/json")


def dump_user_data(request, user_id):
    user = User.objects.get(pk=user_id)
    dump_dict = {}
    for i in range(1000):
        month = randrange(1, 13)
        day = randrange(1,monthrange(2015, month)[1]+1)
        hour = randrange(6, 18)
        minute = randrange(0, 59)
        hour_string = str(hour)
        minute_string = str(minute)
        if hour < 10:
            hour_string = "0" + hour_string
        if minute < 10:
            minute_string = "0" + minute_string
        time = hour_string + ":" + minute_string
        workout_id = randrange(1, 13)
        calories = randrange(100, 500)

        if month not in dump_dict.keys():
            dump_dict[month] = {}
        if day not in dump_dict[month]:
            dump_dict[month][day] = {}
        if time not in dump_dict[month][day]:
            dump_dict[month][day][time] = {}

        dump_dict[month][day][time]["workoutId"] = workout_id
        dump_dict[month][day][time]["calories"] = calories

    user.history = dump_dict
    user.save()
    return HttpResponse("Dump data successfully")


def user_info(request, user_id):
    user = User.objects.get(pk=user_id).as_json()
    return HttpResponse(json.dumps(user, sort_keys=True), content_type="application/json")


def total_calo_week(request, user_id):
    user_history = User.objects.get(pk=user_id).history
    now = datetime.datetime.now() + datetime.timedelta(days=3)
    current_month = now.month
    current_date = now.day
    current_day_of_week = now.weekday()
    recent_list = {}
    date_to_traceback = current_day_of_week - current_date

    if current_date < date_to_traceback:
        previous_month = current_month - 1
        previous_days_to_calculate = monthrange(2015, previous_month)[1] - date_to_traceback
        for i in range(previous_days_to_calculate, monthrange(2015, previous_month)[1]+1):
            sum_of_day = 0
            try:
                for j in user_history[str(previous_month)][str(i)].keys():
                    sum_of_day += user_history[str(previous_month)][str(i)][str(j)]['calories']
                recent_list[datetime.date(2015, previous_month, i).strftime("%A")] = sum_of_day
            except KeyError:
                recent_list[datetime.date(2015, previous_month, i).strftime("%A")] = 0
        for i in range(1, current_date+1):
            sum_of_day = 0
            try:
                for j in user_history[str(current_month)][str(i)].keys():
                    sum_of_day += user_history[str(current_month)][str(i)][j]['calories']
                recent_list[datetime.date(2015, current_month, i).strftime("%A")] = sum_of_day
            except KeyError:
                recent_list[datetime.date(2015, current_month, i).strftime("%A")] = 0
    else:
        start_date = current_date - current_day_of_week
        for i in range(start_date, current_date+1):
            sum_of_day = 0
            try:
                for j in user_history[str(current_month)][str(i)].keys():
                    sum_of_day += user_history[str(current_month)][str(i)][j]['calories']
                recent_list[datetime.date(2015, current_month, i).strftime("%A")] = sum_of_day
            except KeyError:
                recent_list[datetime.date(2015, current_month, i).strftime("%A")] = 0

    return HttpResponse(json.dumps(recent_list, sort_keys=True), content_type="application/json")
