package tasks;

import common.Person;
import common.PersonService;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
Задача 1
Метод на входе принимает List<Integer> id людей, ходит за ними в сервис
(он выдает несортированный Set<Person>, внутренняя работа сервиса неизвестна)
нужно их отсортировать в том же порядке, что и переданные id.
Оценить асимптотику работы
 */
public class Task1 {

  private final PersonService personService;

  public Task1(PersonService personService) {
    this.personService = personService;
  }

/*
Асимптотика: O(n) - для создания personMap и O(m) - для создания упорядоченного списка, итого O(n + m), 
где n - количество элементов в Set<Person>, m - количество id в списке personIds.
*/
  public List<Person> findOrderedPersons(List<Integer> personIds) {
    Set<Person> persons = personService.findPersons(personIds);

    Map<Integer, Person> personMap = persons.stream()
        .collect(Collectors.toMap(Person::id, Function.identity()));
      
    return personIds.stream()
        .map(personMap::get)
        .toList();
  }
}
