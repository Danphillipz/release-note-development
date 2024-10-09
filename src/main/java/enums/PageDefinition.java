package enums;

import exceptions.ConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;
import playwright.pages.BasePage;
import playwright.pages.ExamplePage;


/**
 * Enum representing different pages in the application.
 */
public enum PageDefinition {

  HOME_PAGE("Home", ExamplePage.class);

  private final String name;
  private final Class<? extends BasePage> classDefinition;

  /**
   * Constructs a PageDefinition with the given name and class definition.
   *
   * @param name            The name of the page.
   * @param classDefinition The corresponding class definition.
   */
  PageDefinition(String name, Class<? extends BasePage> classDefinition) {
    this.name = name;
    this.classDefinition = classDefinition;
  }

  /**
   * Gets the PageDefinition based on the provided page name.
   *
   * @param pageName The name of the page.
   * @return The corresponding PageDefinition.
   * @throws ConfigurationException if no matching page is found.
   */
  public static PageDefinition get(String pageName) {
    return Arrays.stream(PageDefinition.values())
        .filter(x -> x.getName().equals(pageName))
        .findFirst()
        .orElseThrow(() -> new ConfigurationException(
            String.format(
                "No page has been configured in the PageDefinition enum with the name %s."
                    + " The following pages have been configured:%s%s",
                pageName, System.lineSeparator(), Arrays.stream(PageDefinition.values()).map(
                    PageDefinition::getName).collect(Collectors.joining(System.lineSeparator())))));
  }

  /**
   * Gets the class definition associated with this PageDefinition.
   *
   * @return The class definition.
   */
  public Class<? extends BasePage> getClassDefinition() {
    return classDefinition;
  }

  /**
   * Checks if the class definition is an instance of the specified class.
   *
   * @param instance The class to check against.
   * @return true if the class definition is an instance of the specified class, false otherwise.
   */
  public boolean isInstanceOf(Class<?> instance) {
    return instance.isAssignableFrom(getClassDefinition());
  }

  /**
   * Casts the class definition to the specified type.
   *
   * @param castTo The target class type.
   * @param <T>    The type to cast to.
   * @return An instance of the specified type.
   */
  public <T> T as(Class<T> castTo) {
    try {
      return castTo.cast(getClassDefinition().getDeclaredConstructor().newInstance());
    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException
             | IllegalAccessException e) {
      throw new ConfigurationException("Unable to cast class", e);
    }
  }

  /**
   * Casts the class definition to the specified type, using Constructor with String param.
   *
   * @param castTo The target class type.
   * @param arg0   Argument to pass to Constructor.
   * @param <T>    The type to cast to.
   * @return An instance of the specified type.
   */
  public <T> T as(Class<T> castTo, String arg0) {
    try {
      return castTo.cast(
          getClassDefinition().getDeclaredConstructor(String.class).newInstance(arg0));
    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException
             | IllegalAccessException e) {
      throw new ConfigurationException("Unable to cast class", e);
    }
  }

  /**
   * Gets the name of the page.
   *
   * @return The page name.
   */
  public String getName() {
    return name;
  }
}
