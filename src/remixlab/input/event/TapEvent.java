/**************************************************************************************
 * bias_tree
 * Copyright (c) 2014-2017 National University of Colombia, https://github.com/remixlab
 * @author Jean Pierre Charalambos, http://otrolado.info/
 *
 * All rights reserved. Library that eases the creation of interactive
 * scenes, released under the terms of the GNU Public License v3.0
 * which is available at http://www.gnu.org/licenses/gpl.html
 **************************************************************************************/

package remixlab.input.event;

import remixlab.input.Event;

/**
 * A tap event encapsulates a {@link TapShortcut} and it's defined
 * by the number of taps. A tap event holds the position where the event occurred (
 * {@link #x()} and {@link #y()}).
 */
public class TapEvent extends Event {
  protected float _x, _y;
  protected int _count;

  /**
   * Constructs a single TapEvent at the given position and from the given
   * gesture-id defining the events {@link #shortcut()}
   *
   * @param x
   * @param y
   * @param id
   */
  public TapEvent(float x, float y, int id) {
    super(NO_MODIFIER_MASK, id);
    this._x = x;
    this._y = y;
    this._count = 1;
  }

  /**
   * Constructs a TapEvent at the given position, from the given gesture-id defining the
   * events {@link #shortcut()}, and with the given number of taps.
   *
   * @param x
   * @param y
   * @param id
   * @param count
   */
  public TapEvent(float x, float y, int id, int count) {
    super(NO_MODIFIER_MASK, id);
    this._x = x;
    this._y = y;
    this._count = count;
  }

  /**
   * Constructs a TapEvent at the given position, from the given gesture-id and
   * modifiers which defines the events {@link #shortcut()}, and with the given number of
   * taps.
   *
   * @param x
   * @param y
   * @param modifiers
   * @param id
   * @param count
   */
  public TapEvent(float x, float y, int modifiers, int id, int count) {
    super(modifiers, id);
    this._x = x;
    this._y = y;
    this._count = count;
  }

  protected TapEvent(TapEvent other) {
    super(other);
    this._x = other._x;
    this._y = other._y;
    this._count = other._count;
  }

  @Override
  public TapEvent get() {
    return new TapEvent(this);
  }

  @Override
  public TapEvent flush() {
    return (TapEvent) super.flush();
  }

  @Override
  public TapEvent fire() {
    return (TapEvent) super.fire();
  }

  @Override
  public TapShortcut shortcut() {
    return new TapShortcut(modifiers(), id(), count());
  }

  /**
   * @return event x coordinate
   */
  public float x() {
    return _x;
  }

  /**
   * @return event y coordinate
   */
  public float y() {
    return _y;
  }

  /**
   * @return event number of taps
   */
  public int count() {
    return _count;
  }
}
