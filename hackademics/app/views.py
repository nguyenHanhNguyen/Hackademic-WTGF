from django.http import HttpResponse, JsonResponse
from models import Category, Workout, User
import json


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


def add_workout(request, category_id, name, estimated_calories, description, video_url, tags):
    the_category = Category.objects.get(pk=category_id)
    new_workout = Workout()
    new_workout.name = name
    new_workout.estimated_calories = estimated_calories
    new_workout.description = description
    new_workout.video_url = video_url
    new_workout.tags = tags
    new_workout.save()
    the_category.exerciseNumber += 1
    the_category.save()