from random import randrange
from django.http import HttpResponse
from models import Category, Workout, User
import json
import calendar
import collections


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
        cal = calendar.Calendar()
        day = randrange(1,28)
        hour = randrange(6, 18)
        minute = randrange(0, 59)
        time = str(hour) + ":" + str(minute)
        workout_id = randrange(1,13)
        calories = randrange(100,500)

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
    return HttpResponse(json.dumps(user), content_type="application/json")